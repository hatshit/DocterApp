package com.tac.reportingDemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
import com.tac.reportingDemo.adapter.DoctorAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDoctorForReportActivity extends AppCompatActivity {
    @BindView(R.id.searchEt)
    EditText mSearchEt;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.parent)
    RelativeLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    MySharedPreferences sp;
    RequestQueue mRequestQue;
    private String TAG = "TAC";
    private DoctorAdapter adapter;
    private List<AreaPojo> mList = new ArrayList<>();
    private String area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);

        ButterKnife.bind(this);
        mSearchEt.setHint("Search Doctor");


        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        area = sp.getUserInfo("areaName");

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Select Doctor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new DoctorAdapter(SelectDoctorForReportActivity.this,
                mList, 2);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.DOCTOR_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR AREA onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");

                        mList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            AreaPojo pojo = new AreaPojo();
                            JSONObject data = dataArray.getJSONObject(i);
                            pojo.setId(data.getString("Did"));
                            pojo.setName(data.getString("DName"));

                            mList.add(pojo);

                        }
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.makeToast("Oops! Something Went Wrong.");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR AREA error: ");
                Utils.makeToast("Oops! Something Went Wrong.");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("city", area);
                params.put("Rid",sp.getUserInfo(Constants.R_ID));

                Log.d(TAG, "MUR DOCTOR getParams: " + params);
                return params;
            }
        };

        mRequestQue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SelectAreaActivity.class);
        startActivity(intent);
    }
}
