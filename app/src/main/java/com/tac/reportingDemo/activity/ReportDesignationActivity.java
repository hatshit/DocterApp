package com.tac.reportingDemo.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.adapter.MyChemistsAdapter;
import com.tac.reportingDemo.adapter.ReportAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.pojo.DoctorReportItem;
import com.tac.reportingDemo.pojo.MyDoctorPojo;
import com.tac.reportingDemo.pojo.ProductPojoItem1;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportDesignationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.rgType)
    RadioGroup rgType;
    @BindView(R.id.spinner)
    AutoCompleteTextView spinner;
    @BindView(R.id.btnList)
    ImageView btnList;
    @BindView(R.id.layoutFromDate)
    LinearLayout layoutFromDate;

    @BindView(R.id.layoutToDate)
    LinearLayout layoutToDate;

    @BindView(R.id.tvFromDate)
    TextView tvFromDate;

    @BindView(R.id.tvToDate)
    TextView tvToDate;
    @BindView(R.id.pb)
    ProgressBar mPb;

    @BindView(R.id.viewDetails)
    Button mViewDetails;

    @BindView(R.id.txtEmploye)
    TextView txtEmploye;

    @BindView(R.id.txtSelf)
    TextView txtSelf;

    @BindView(R.id.layoutArea)
    LinearLayout layoutArea;
    private RequestQueue mRequestQue;
    private MySharedPreferences sp;
    private int mYear, mMonth, mDay, mHour, mMinute;

    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog picker;
    String strFromDate = "", strToDate = "", strType = "", empRid = "";
    ArrayAdapter areaAdapter;
    private List<DoctorReportItem> mList = new ArrayList<>();
    private List<MyDoctorPojo> mEmployeList = new ArrayList<>();
    private ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_desig);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        setToolbar();
        adapter = new ReportAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbChemist) {
                    strType = "chemist";
                } else if (checkedId == R.id.rbDoctor) {
                    strType = "doctor";
                }

            }
        });

        txtSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSelf.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtEmploye.setTextColor(getResources().getColor(R.color.white));

                empRid =sp.getUserInfo(Constants.R_ID);
                txtEmploye.setBackgroundResource(R.drawable.button_background);
                txtSelf.setBackgroundResource(R.drawable.btn_line_border);
                layoutArea.setVisibility(View.GONE);
            }
        });

        txtEmploye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSelf.setTextColor(getResources().getColor(R.color.white));
                txtEmploye.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtEmploye.setBackgroundResource(R.drawable.btn_line_border);
                txtSelf.setBackgroundResource(R.drawable.button_background);
                layoutArea.setVisibility(View.VISIBLE);
            }
        });

        mViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });


        layoutFromDate.setOnClickListener(v -> {

            mYear = myCalendar.get(Calendar.YEAR);
            mMonth = myCalendar.get(Calendar.MONTH);
            mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            picker = new DatePickerDialog(ReportDesignationActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            String temp = "" + day + "/" + (month + 1) + "/" + year;
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

                            try {
                                Date date = formatter.parse(temp);
                                strFromDate = formatter.format(date);
                                tvFromDate.setText(strFromDate);

                               // checkValidation();
                            } catch (Exception ex) {

                            }
                        }
                    }, mYear, mMonth, mDay);

            picker.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
            picker.show();
        });

        layoutToDate.setOnClickListener(v -> {

            mYear = myCalendar.get(Calendar.YEAR);
            mMonth = myCalendar.get(Calendar.MONTH);
            mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            picker = new DatePickerDialog(ReportDesignationActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    (view, year, month, day) -> {
                        String temp = "" + day + "/" + (month + 1) + "/" + year;
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

                        try {
                            Date date = formatter.parse(temp);
                            strToDate = formatter.format(date);
                            tvToDate.setText(strToDate);

                          //  checkValidation();
                        } catch (Exception ex) {

                        }
                    }, mYear, mMonth, mDay);

            picker.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
            picker.show();
        });


        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.showDropDown();
            }
        });
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    empRid = ((MyDoctorPojo) areaAdapter.getItem(i)).getId();
                  //  checkValidation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        areaAdapter = new ArrayAdapter(ReportDesignationActivity.this, android.R.layout.simple_spinner_item);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fetchEmployeeList();
    }

    private void checkValidation() {
        if (strType.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select designation", Toast.LENGTH_SHORT).show();
        } else if (strFromDate.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select from date", Toast.LENGTH_SHORT).show();
        } else if (strToDate.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select to date", Toast.LENGTH_SHORT).show();
        } else if (empRid.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select to employee", Toast.LENGTH_SHORT).show();
        } else if (strType.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select to type Docter and chemist.", Toast.LENGTH_SHORT).show();
        } else {
            checkNetworkAndFetchData();
        }
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void checkNetworkAndFetchData() {
        if (Utils.isNetworkAvailable()) {
            fetchData();
        } else {
            Utils.noInternetToast();
        }
    }

    private void fetchEmployeeList() {
        Utils.showPB(mRecyclerView, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.EMPLOYEE_LIST, response -> {
            mEmployeList.clear();
            Utils.hidePB(mRecyclerView, mPb);
            try {
                if (JsonParsor.isReqSuccesful(response)) {
                    JSONObject object = new JSONObject(response);
                    JSONArray dataArray = object.getJSONArray("Data");
                    areaAdapter.clear();
                    mEmployeList.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject objectItem = dataArray.getJSONObject(i);
                        MyDoctorPojo mdp = new MyDoctorPojo(objectItem.getString("EmpName"), objectItem.getString("Rid"));
                        mEmployeList.add(mdp);
                        areaAdapter.add(mdp);
                    }
                    spinner.setAdapter(areaAdapter);
                } else {
                    Utils.makeToast(JsonParsor.simpleParser(response));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.parsingErrorToast();
            }
        }, error -> {
            Utils.hidePB(mRecyclerView, mPb);
            Utils.parsingErrorToast();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("Post", "area sales manager");
                return params;

            }
        };
        mRequestQue.add(request);
    }

    private void fetchData() {
        Utils.showPB(mRecyclerView, mPb);
        String url = strType.equalsIgnoreCase("chemist") ? ENDPOINTS.REPORT_CHEMIST_DESIG_LIST : ENDPOINTS.REPORT_DOCTOR_DESIG_LIST;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            mList.clear();
            Utils.hidePB(mRecyclerView, mPb);
            try {
                if (JsonParsor.isReqSuccesful(response)) {
                    JSONObject object = new JSONObject(response);
                    JSONArray dataArray = object.getJSONArray("Data");
                    if (strType.equalsIgnoreCase("chemist")) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);
                            ArrayList<ProductPojoItem1> list = new ArrayList<>();

                            // Check if "productreports" exists and is not null
                            if (data.has("productreports") && !data.isNull("productreports")) {
                                JSONArray arrayProduct = data.getJSONArray("productreports");
                                for (int j = 0; j < arrayProduct.length(); j++) {
                                    JSONObject dataObj = arrayProduct.getJSONObject(j);
                                    list.add(new ProductPojoItem1(dataObj.getString("rptid"), dataObj.getString("productid"), dataObj.getString("productname"), dataObj.getString("productqty"), dataObj.getString("productamount"), dataObj.getString("totamount")));
                                }
                            }

                            DoctorReportItem pojo = new DoctorReportItem(data.getString("rptid"),
                                    data.getString("loginid"),
                                    data.getString("areaname"),
                                    data.getString("chemistname"),
                                    data.getString("reportdate"),
                                    data.getString("withwhom"),
                                    data.getString("remark"),
                                    data.getString("latitude"),
                                    data.getString("longitude"), list);
                            mList.add(pojo);
                        }

                        adapter.notifyDataSetChanged();
                    } else if (strType.equalsIgnoreCase("doctor")) {

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);
                            ArrayList<ProductPojoItem1> list = new ArrayList<>();

                            // Check if "productreports" exists and is not null
                            if (data.has("productreports") && !data.isNull("productreports")) {
                                JSONArray arrayProduct = data.getJSONArray("productreports");
                                for (int j = 0; j < arrayProduct.length(); j++) {
                                    JSONObject dataObj = arrayProduct.getJSONObject(j);
                                    list.add(new ProductPojoItem1(dataObj.getString("rptid"), dataObj.getString("productid"), dataObj.getString("productname"), dataObj.getString("productqty"), dataObj.getString("productamount"), dataObj.getString("totamount")));
                                }
                            }

                            DoctorReportItem pojo = new DoctorReportItem(data.getString("rptid"),
                                    data.getString("loginid"),
                                    data.getString("areaname"),
                                    data.getString("doctorname"),
                                    data.getString("reportdate"),
                                    data.getString("withwhom"),
                                    data.getString("remark"),
                                    data.getString("latitude"),
                                    data.getString("longitude"), list);
                            mList.add(pojo);
                        }

//                        for (int i = 0; i < dataArray.length(); i++) {
//                            JSONObject data = dataArray.getJSONObject(i);
//                            ArrayList<ProductPojoItem1> list = new ArrayList<>();
//                            JSONArray arrayProduct = data.getJSONArray("productreports");
//                            for (int j = 0; j < arrayProduct.length(); j++) {
//                                JSONObject dataObj = arrayProduct.getJSONObject(j);
//                                list.add(new ProductPojoItem1(dataObj.getString("rptid"), dataObj.getString("productid"), dataObj.getString("productname"), dataObj.getString("productqty"), dataObj.getString("productamount"), dataObj.getString("totamount")));
//                            }
//                            DoctorReportItem pojo = new DoctorReportItem(data.getString("rptid"),
//                                    data.getString("loginid"),
//                                    data.getString("areaname"),
//                                    data.getString("doctorname"),
//                                    data.getString("reportdate"),
//                                    data.getString("withwhom"),
//                                    data.getString("remark"),
//                                    data.getString("latitude"),
//                                    data.getString("longitude"), list);
//                            mList.add(pojo);
//                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Utils.makeToast(JsonParsor.simpleParser(response));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.parsingErrorToast();
            }
        }, error -> {
            Utils.hidePB(mRecyclerView, mPb);
            Utils.parsingErrorToast();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("RId", empRid);
                params.put("fromdate", strFromDate);
                params.put("todate", strToDate);
                return params;

            }
        };
        mRequestQue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
