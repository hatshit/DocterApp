package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.adapter.ReportsAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ReportPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorsReportActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.parent)
    RelativeLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.no_report)
    TextView mNoReports;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    private List<ReportPojo> mList = new ArrayList<>();
    private ReportsAdapter adapter;

    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_report);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("DCR Report");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = MySharedPreferences.getInstance(this);

        date = sp.getReportInfo("date");

        mRequestQue = MyVolley.getInstance().getRequestQueue();


        adapter = new ReportsAdapter(this, mList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        checkNetworkAndGetReports();


    }

    private void checkNetworkAndGetReports() {
        if (Utils.isNetworkAvailable()) {
            getReports();
        } else {
            Utils.noInternetToast();
        }
    }

    private void getReports() {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.GET_DCR_REPORT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR DCR report onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("Data");
                        mList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);
                            ReportPojo pojo = new ReportPojo();
                            pojo.setDoctorName(data.getString("doctorname"));
                            pojo.setAreaName(data.getString("areaname"));
                            pojo.setGifts(data.get("giftreports").toString());
                            pojo.setProducts(data.get("productreports").toString());
                            pojo.setSamples(data.get("samplereports").toString());
                            pojo.setId(data.getString("rptid"));
                            pojo.setDate(data.getString("reportdate"));
                            mList.add(pojo);
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        mParent.setVisibility(View.GONE);
                        mNoReports.setVisibility(View.VISIBLE);
//                        Utils.makeToast(JsonParsor.simpleParser(response));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mParent, mPb);
                Utils.parsingErrorToast();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("loginid", sp.getUserInfo(Constants.USERNAME));
                params.put("reportdate", date);

                Log.d(TAG, "MUR REPORT getParams: " + params);
                return params;
            }
        };
        mRequestQue.add(request);
    }
    }
