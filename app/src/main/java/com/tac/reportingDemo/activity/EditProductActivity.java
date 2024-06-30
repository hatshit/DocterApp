package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.chemist.ChemistBillingActivity;
import com.tac.reportingDemo.adapter.EditProductAdapter;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ProductPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProductActivity extends AppCompatActivity {

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
    @BindView(R.id.add_more)
    RelativeLayout mAddMore;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private EditProductAdapter adapter;
    private List<ProductPojo> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Products");

        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditProductAdapter(this, mList);

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
        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProductActivity.this, AddProductsActivity.class);
                startActivity(intent);
            }
        });

        fetchProducts();
    }

    public void fetchProducts() {
        try {

            List<String> productIds = new Gson().fromJson(sp.getProductId(), ArrayList.class);
            JSONObject dataArray = new JSONObject(sp.getProduct());
            mList.clear();
            for (int i = 0; i < productIds.size(); i++) {
                JSONObject data = dataArray.getJSONObject(productIds.get(i));
                ProductPojo pojo = new ProductPojo();
                pojo.setName(data.getString("name"));
                pojo.setId(data.getString("id"));
                pojo.setAmount(data.getString("amount"));
                pojo.setQty(data.getString("qty"));
                pojo.setTotal(data.getString("total"));

                mList.add(pojo);
            }
            adapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
