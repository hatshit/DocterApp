package com.tac.reportingDemo.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.adapter.MyDoctorsAdapter;
import com.tac.reportingDemo.adapter.ViewPlanAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.MyDoctorPojo;
import com.tac.reportingDemo.pojo.MyViewPlanPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPlanAct extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.spinnerYear)
    Spinner spinnerYear;

    @BindView(R.id.spinnerMonth)
    Spinner spinnerMonth;

    @BindView(R.id.submitPlan)
    Button submitPlan;
    @BindView(R.id.txtEmty)
    TextView txtEmty;

    @BindView(R.id.spinnerDepartmentAdd)
    Spinner spinnerDepartmentAdd;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    private ViewPlanAdapter adapterViewPlan;

    private List<MyViewPlanPojo> mList = new ArrayList<>();

    private String strDepartMent_ID = "";

    private String strDay="", strDepartMent="", strWeek="", strMonth="",stryear="",strdDate="";

    List<String> departMentList = List.of("Select department", "Doctor", "Chemist");

    List<String> year = List.of("Select Year", "2024", "2025", "2026", "2027", "2028", "2029", "2030");
    List<String> monthList = List.of("Select Month", "jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plan);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        setToolbar();

        setSpinner(spinnerYear,year);
        setSpinner(spinnerMonth,monthList);
        setSpinner(spinnerDepartmentAdd,departMentList);

        adapterViewPlan = new ViewPlanAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        intintFuc();
    }

    private void intintFuc() {

        submitPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkNetworkAndFetchData();
            }
        });
        spinnerDepartmentAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = parent.getItemAtPosition(position).toString();
                if (!selectedDepartment.equals("Select department")) {
                    strDepartMent =selectedDepartment;

                    if(strDepartMent.equals("Chemist")) {
                        strDepartMent_ID = "2";
                    }else {
                        strDepartMent_ID = "1";
                    }
                    // Do something with the selected value
                    // Toast.makeText(getApplicationContext(), "Select Department: " + selectedDepartment, Toast.LENGTH_SHORT).show();
                }else {
                    strDepartMent ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected, if needed
            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedWeek = parent.getItemAtPosition(position).toString();
                if (!selectedWeek.equals("Select Year")) {
                    stryear =selectedWeek;
                    // Do something with the selected value
                }else {
                    stryear ="";
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
                }else {
                    strMonth ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected, if needed
            }
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
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.MY_VIEW_PLAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList.clear();
                Utils.hidePB(mRecyclerView, mPb);
                try {
                  if (JsonParsor.isReqSuccesful(response)) {
                      txtEmty.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("Data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);
                            MyViewPlanPojo pojo = new MyViewPlanPojo();
                            int planIdValue = data.optInt("planId", 0);
                            pojo.setType(Integer.toString(planIdValue));

                            pojo.setPlanId(data.getString("planId"));
                            pojo.setAreaname(data.getString("areaname"));
                            pojo.setName(data.getString("Name"));
                            pojo.setYear(data.getString("year"));
                            pojo.setMonth(data.getString("month"));
                            pojo.setWeek(data.getString("week"));
                            pojo.setDay(data.getString("day"));
                            pojo.setRemark(data.getString("remark"));
                            pojo.setPlandate(data.getString("Plandate"));

                            int typeValue = data.optInt("type", 0);
                            pojo.setType(Integer.toString(typeValue));

                            mList.add(pojo);
                        }
                        mRecyclerView.setAdapter(adapterViewPlan);
                        adapterViewPlan.notifyDataSetChanged();
                    } else {
                       txtEmty.setVisibility(View.VISIBLE);
                        // Utils.makeToast("No Data Available");
                    }
                } catch (Exception e) {
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
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("month",strMonth);
                params.put("year",stryear);
                params.put("type",strDepartMent_ID);
                return params;
            }
        };
        mRequestQue.add(request);
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

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("View Plan");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change the color of the back button
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}