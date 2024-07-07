package com.tac.reportingDemo.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.adapter.ProductListAdapter;
import com.tac.reportingDemo.adapter.ReportAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
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

public class ProductListActivity extends AppCompatActivity {
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

    private List<ProductPojoItem1> mList = new ArrayList<>();

    private ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_desig);
        ButterKnife.bind(this);
        mList.addAll((ArrayList<ProductPojoItem1>)getIntent().getSerializableExtra("product"));
        setToolbar();
        adapter = new ProductListAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        rgType.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        btnList.setVisibility(View.GONE);
        layoutFromDate.setVisibility(View.GONE);
        layoutToDate.setVisibility(View.GONE);
        tvFromDate.setVisibility(View.GONE);
        tvToDate.setVisibility(View.GONE);
        mPb.setVisibility(View.GONE);

    }


    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Report");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbaar caolor
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
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
