package com.tac.reportingDemo.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.chemist.ChemistBillingActivity;
import com.tac.reportingDemo.adapter.EditGiftAdapter;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ProductPojo;
import com.tac.reportingDemo.storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditGiftActivity extends AppCompatActivity {

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
    private EditGiftAdapter adapter;
    private List<ProductPojo> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Gifts");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

     //toolbaar caolor
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditGiftAdapter(this, mList);

        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditGiftActivity.this, AddGiftActivity.class);
                startActivity(intent);
            }
        });
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

        fetchProducts();
    }

    public void fetchProducts() {
        try {

            List<String> productIds = new Gson().fromJson(sp.getGiftIds(), ArrayList.class);
            JSONObject dataArray = new JSONObject(sp.getGifts());
            mList.clear();
            for (int i = 0; i < productIds.size(); i++) {
                JSONObject data = dataArray.getJSONObject(productIds.get(i));
                ProductPojo pojo = new ProductPojo();
                pojo.setName(data.getString("name"));
                pojo.setId(data.getString("id"));
//                pojo.setAmount(data.getString("amount"));
                pojo.setQty(data.getString("qty"));
//                pojo.setTotal(data.getString("total"));

                mList.add(pojo);
            }
            adapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
