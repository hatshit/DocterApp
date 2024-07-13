package com.tac.reportingDemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.adapter.AreaAdapter;
import com.tac.reportingDemo.adapter.DocterAdapterList;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPlanAct extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.toolbar1)
    Toolbar mToolbar;
    @BindView(R.id.spinnerDepartmentAdd)
    Spinner spinnerDepartmentAdd;

    @BindView(R.id.spinnerMonth)
    Spinner spinnerMonth;

    @BindView(R.id.spinnerDay)
    Spinner spinnerDay;

    @BindView(R.id.spinnerWeek)
    Spinner spinnerWeek;

    @BindView(R.id.edtDocter)
    EditText mDocter;
    @BindView(R.id.areaAddPlan)
    EditText mAreaAddPlan;


    @BindView(R.id.parent)
    LinearLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;

    private List<AreaPojo> mDoctorList = new ArrayList<>();
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    // Example of dynamic data
    List<String> departMentList = List.of("Select department", "Doctor", "Chemist");
    List<String> weekList = List.of("Select Week", "1", "2", "3", "4");
    List<String> DayList = List.of("Select Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
    List<String> monthList = List.of("Select Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

    String strArea="";

    private DocterAdapterList drAdapter;

   private AreaAdapter adapter;
    List<AreaPojo> docterList = new ArrayList<>();
    List<AreaPojo> areaList = new ArrayList<>();
    private String strDay="", strDepartMent="", strWeek="", strMonth="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        strArea =  sp.getHq_ame(Constants.HQ_name);

        setToolbar();

        drAdapter = new DocterAdapterList(AddPlanAct.this, docterList);
        adapter = new AreaAdapter(AddPlanAct.this, areaList, 6);

        setSpinner(spinnerDepartmentAdd,departMentList);
        setSpinner(spinnerWeek,weekList);
        setSpinner(spinnerMonth,monthList);
        setSpinner(spinnerDay,DayList);

        initFuc();
    }

    private void initFuc() {
        mAreaAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreaDialog();
               mAreaAddPlan.setError(null);
            }
        });

        mDocter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strDepartMent.equals(""))
                {
                    Toast.makeText(AddPlanAct.this, "Please select department", Toast.LENGTH_SHORT).show();
                }else if(mAreaAddPlan.getText().toString().equals("")){
                    Toast.makeText(AddPlanAct.this, "Please select Area", Toast.LENGTH_SHORT).show();
                }else {
                    showDocterDialog();
                    mDocter.setError(null);
                }
            }
        });

        // Set listeners to get selected values
        spinnerDepartmentAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = parent.getItemAtPosition(position).toString();
                if (!selectedDepartment.equals("Select department")) {
                    strDepartMent =selectedDepartment;
                    // Do something with the selected value
                    Toast.makeText(getApplicationContext(), "Select Department: " + selectedDepartment, Toast.LENGTH_SHORT).show();
                }else {
                    strDepartMent ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected, if needed
            }
        });

        spinnerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedWeek = parent.getItemAtPosition(position).toString();
                if (!selectedWeek.equals("Select Week")) {
                    strWeek =selectedWeek;
                    // Do something with the selected value
                    Toast.makeText(getApplicationContext(), "Selected Week: " + selectedWeek, Toast.LENGTH_SHORT).show();
                }else {
                    strWeek ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected, if needed
            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
                if (!selectedMonth.equals("Select Month")) {
                    strMonth =selectedMonth;
                    // Do something with the selected value
                    Toast.makeText(getApplicationContext(), "Selected Month: " + selectedMonth, Toast.LENGTH_SHORT).show();
                }else {
                    strMonth ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected, if needed
            }
        });

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                if (!selectedDay.equals("Select Day")) {
                    strDay =selectedDay;
                    // Do something with the selected value
                    Toast.makeText(getApplicationContext(), "Select Day: " + selectedDay, Toast.LENGTH_SHORT).show();
                }else {
                    strDay ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected, if needed
            }
        });


    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Plan");
            mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Change the color of the back button
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        } else {
            // Handle the null case here
            Log.e("AddPlanAct", "Support Action Bar is null");
        }
    }

    private void setSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle selection
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
    }

    AlertDialog dialog;
    private void showAreaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Area");

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        View view = LayoutInflater.from(this).inflate(R.layout.show_report_dialog_new, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        EditText edtDocterSearch = view.findViewById(R.id.edtDocterSearch);
        ProgressBar pb = view.findViewById(R.id.pb);
        getApiCall(recyclerView, pb,"area");

        edtDocterSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.getFilter().filter(charSequence.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }


    public void setArea(int position) {
        dialog.dismiss();
        String areId =areaList.get(position).getId();
        strArea =areaList.get(position).getName();
        mAreaAddPlan.setText(strArea);
        mDocter.setText("");
    }

    public void setDocter(int position) {
        dialog.dismiss();
        String doctorId =areaList.get(position).getId();
        String doctorName =docterList.get(position).getName();
        mDocter.setText(doctorName);
    }


    private void showDocterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Doctor");

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDocter.setText("");
                dialog.dismiss();
            }
        });

        View view = LayoutInflater.from(this).inflate(R.layout.show_report_dialog_new, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        EditText edtDocterSearch = view.findViewById(R.id.edtDocterSearch);
        ProgressBar pb1 = view.findViewById(R.id.pb);
        getApiCall(recyclerView, pb1,"doctor");
        edtDocterSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (drAdapter != null) {
                    drAdapter.getFilter().filter(charSequence.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void getApiCall(RecyclerView recyclerView, ProgressBar pb, String type) {
        String url;
        Map<String, String> params = new HashMap<>();
        Response.Listener<String> responseListener;
        Response.ErrorListener errorListener;

        // Setup common RecyclerView configurations
        Utils.showPB(recyclerView, pb);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Setup URL, Params and Response Handlers based on type
        if (type.equals("area")) {
            url = ENDPOINTS.AREA_LIST;
            params.put("Rid", sp.getUserInfo(Constants.R_ID));

            responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utils.hidePB(recyclerView, pb);
                    Log.d(TAG, "AREA onResponse: " + response);
                    try {
                        if (JsonParsor.isReqSuccesful(response)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray dataArray = object.getJSONArray("data");

                            areaList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                AreaPojo pojo = new AreaPojo();
                                JSONObject data = dataArray.getJSONObject(i);
                                pojo.setId(data.getString("AreaId"));
                                pojo.setName(data.getString("Area"));
                                areaList.add(pojo);
                            }
                            adapter = new AreaAdapter(AddPlanAct.this, areaList, 6);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            dialog.dismiss();
                            Utils.makeToast(JsonParsor.simpleParser(response));
                        }
                    } catch (JSONException e) {
                        Utils.makeToast("Oops! Something Went Wrong.");
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            };

            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utils.hidePB(recyclerView, pb);
                    dialog.dismiss();
                    Log.d(TAG, "AREA error: " + error.toString());
                    Utils.makeToast("Oops! Something Went Wrong.");
                }
            };

        } else {
            url = ENDPOINTS.DOCTOR_LIST;
            params.put("city", strArea);
            params.put("Rid", sp.getUserInfo(Constants.R_ID));
            try{
                if(strDepartMent.equals("Chemist"))
                {
                    params.put("Deptid","2");
                }else {
                    params.put("Deptid","1");
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }


            responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utils.hidePB(recyclerView, pb);
                    Log.d(TAG, "DOCTOR onResponse: " + response);
                    try {
                        if (JsonParsor.isReqSuccesful(response)) {
                            JSONObject object = new JSONObject(response);
                            JSONArray dataArray = object.getJSONArray("data");

                            docterList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                AreaPojo pojo = new AreaPojo();
                                JSONObject data = dataArray.getJSONObject(i);
                                pojo.setId(data.getString("Did"));
                                pojo.setName(data.getString("DName"));
                                docterList.add(pojo);
                            }
                            drAdapter = new DocterAdapterList(AddPlanAct.this, docterList);
                            recyclerView.setAdapter(drAdapter);
                            drAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "error: " + JsonParsor.simpleParser(response));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Utils.hidePB(recyclerView, pb);
                    Log.d(TAG, "DOCTOR error: " + error.toString());
                    Utils.makeToast("Oops! Something Went Wrong.");
                }
            };
        }

        // Make the network request
        StringRequest request = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQue.add(request);
    }
}