package com.tac.reportingDemo.activity;

import android.os.Bundle;
import android.widget.ProgressBar;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyChemistActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private List<MyDoctorPojo> mList = new ArrayList<>();
    private MyChemistsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doctors);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        setToolbar();

        adapter = new MyChemistsAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkNetworkAndFetchData();
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
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.MY_CHEMISTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mList.clear();
                Utils.hidePB(mRecyclerView, mPb);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("Data");
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
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                return params;
            }
        };
        mRequestQue.add(request);
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("My Chemists");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
