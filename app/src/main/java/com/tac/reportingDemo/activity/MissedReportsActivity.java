package com.tac.reportingDemo.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.MyDoctorPojo;
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

public class MissedReportsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.rgType)
    RadioGroup rgType;
    @BindView(R.id.tvFromDate)
    TextView tvFromDate;
    @BindView(R.id.tvToDate)
    TextView tvToDate;
    @BindView(R.id.layoutFromDate)
    LinearLayout layoutFromDate;
    @BindView(R.id.layoutToDate)
    LinearLayout layoutToDate;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private List<MyDoctorPojo> mList = new ArrayList<>();
    private MyChemistsAdapter adapter;


    private int mYear, mMonth, mDay, mHour, mMinute;

    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog picker;
    String strFromDate="",strToDate="",strType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        setToolbar();

        adapter = new MyChemistsAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if(checkedId == R.id.rbChemist){
                    strType = "chemist";
                } else if(checkedId == R.id.rbDoctor){
                    strType = "doctor";
                }

                if(strFromDate.trim().length()>3 && strToDate.trim().length()>3 ){
                checkNetworkAndFetchData();
                }else if(strFromDate.trim().length()<3) {
                    Toast.makeText(getApplicationContext(), "Please select from date", Toast.LENGTH_SHORT).show();
                } else if(strToDate.trim().length()<3) {
                    Toast.makeText(getApplicationContext(), "Please select to date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutFromDate.setOnClickListener(v -> {

            mYear = myCalendar.get(Calendar.YEAR);
            mMonth = myCalendar.get(Calendar.MONTH);
            mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

            picker = new DatePickerDialog(MissedReportsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            String temp = "" + day+ "/" + (month + 1) + "/" +  year;
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

                            try {
                                Date date = formatter.parse(temp);
                                strFromDate = formatter.format(date);
                                tvFromDate.setText(strFromDate);

                                if(strFromDate.trim().length()>3 && strToDate.trim().length()>3 && strType.length()>3 ){
                                    checkNetworkAndFetchData();
                                }
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

            picker = new DatePickerDialog(MissedReportsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    (view, year, month, day) -> {
                        String temp = "" + day+ "/" + (month + 1) + "/" +  year;
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");

                        try {
                            Date date = formatter.parse(temp);
                            strToDate = formatter.format(date);
                            tvToDate.setText(strToDate);

                            if(strFromDate.trim().length()>3 && strToDate.trim().length()>3 && strType.length()>3 ){
                                checkNetworkAndFetchData();
                            }
                        } catch (Exception ex) {

                        }
                    }, mYear, mMonth, mDay);

            picker.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());
            picker.show();
        });

    }

    private void checkNetworkAndFetchData() {
        if (Utils.isNetworkAvailable()) {
            fetchData();
        } else {
            Utils.noInternetToast();
        }
    }

    private void fetchData() {
        Utils.showPB(mRecyclerView, mPb);

        String url = "";
        if (strType.equalsIgnoreCase("chemist")) {
            url = ENDPOINTS.GET_CHEMIST_MISSED_REPORTS;
        } else if (strType.equalsIgnoreCase("doctor")) {
            url = ENDPOINTS.GET_DOCTOR_MISSED_REPORTS;
        }
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList.clear();
                Utils.hidePB(mRecyclerView, mPb);
                try {

                    /*
                    *
 {"result":true,"message":"success","Data":[
 * {"DRName":"dr.snehal Saudager",
 * "HospitalName":"dogs n pups pet clinic",
 * "Address":"shop no.7 Mumbai- 400706",
* "MobileNo":"8879904405"}]}*/
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);

                        JSONArray dataArray = object.getJSONArray("Data");
                        if(strType.equalsIgnoreCase("chemist")) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject data = dataArray.getJSONObject(i);
                                MyDoctorPojo pojo = new MyDoctorPojo();
                                pojo.setHospital(data.getString("FirmName"));
                                pojo.setMobile(data.getString("MobileNo"));
                                pojo.setName(data.getString("ChemistName"));
                                pojo.setId(data.getString("CId"));
                                mList.add(pojo);
                            }
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }  else    if(strType.equalsIgnoreCase("doctor")) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject data = dataArray.getJSONObject(i);
                                MyDoctorPojo pojo = new MyDoctorPojo();
                                pojo.setHospital(data.getString("HospitalName"));
                                pojo.setMobile(data.getString("MobileNo"));
                                pojo.setName(data.getString("DRName"));
                                mList.add(pojo);
                            }
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.parsingErrorToast();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mRecyclerView, mPb);
                Utils.parsingErrorToast();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Rid", "1005");
                params.put("fromdate", "01/06/2021");
                params.put("todate", "01/07/2021");

                return params;

            }
        };
        mRequestQue.add(request);
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Missed Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
