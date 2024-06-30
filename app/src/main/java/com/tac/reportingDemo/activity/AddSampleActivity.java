package com.tac.reportingDemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.chemist.ChemistBillingActivity;
import com.tac.reportingDemo.adapter.AddSampleAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddSampleActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_products)
    RelativeLayout mAddBtn;
    @BindView(R.id.products)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.parent)
    RelativeLayout mParent;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private AddSampleAdapter adapter;
    private List<AreaPojo> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        ButterKnife.bind(this);


        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Samples");

        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddSampleAdapter(this, mList);
        checkNetworkAndFetchProduct();

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getBillingType().equals("chemist")) {
                    Intent intent = new Intent(getApplicationContext(), ChemistBillingActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), BillingActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void checkNetworkAndFetchProduct() {

        if (Utils.isNetworkAvailable()) {
            fetchProducts();
        } else {
            Utils.noInternetToast();
        }
    }

    private void fetchProducts() {

        Utils.showPB(mParent, mPb);

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.GET_PRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR products onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {

                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");
                        mList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject data = dataArray.getJSONObject(i);
                            AreaPojo pojo = new AreaPojo();
                            pojo.setName(data.getString("ProductName"));
                            pojo.setId(data.getString("ProductId"));
                            mList.add(pojo);
                        }
                        adapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mParent, mPb);

            }
        });
        mRequestQue.add(request);
    }

    public void removeView(int position) {

        mList.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddSampleActivity.this,BillingActivity.class));
    }
}
