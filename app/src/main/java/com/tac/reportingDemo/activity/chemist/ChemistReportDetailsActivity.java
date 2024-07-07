package com.tac.reportingDemo.activity.chemist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.HomeActivity;
import com.tac.reportingDemo.adapter.ProductsInReportAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ProductPojo;
import com.tac.reportingDemo.pojo.ReportPojo;
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

public class ChemistReportDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.parent)
    RelativeLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.no_report)
    TextView mNoReports;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.viewProducts)
    LinearLayout mViewProducts;
    @BindView(R.id.products)
    LinearLayout productParent;
    @BindView(R.id.productsAddedText)
    TextView mProductAddedText;
    @BindView(R.id.productsAddedImg)
    ImageView mProductAddedImg;

    @BindView(R.id.giftsAddedText)
    TextView mGiftAddedText;
    @BindView(R.id.giftAddedImg)
    ImageView mGiftAddedImg;
    @BindView(R.id.sampleAddedText)
    TextView mSampleAddedText;
    @BindView(R.id.sampleAddedImg)
    ImageView mSampleAddedImg;
//    @BindView(R.id.amount)
//    Button mAmount;
//    @BindView(R.id.name)
//    Button mName;
//    @BindView(R.id.qty)
//    Button mQty;

    @BindView(R.id.productsAdded)
    LinearLayout mProductsAdded;


    @BindView(R.id.view_gifts)
    LinearLayout mViewGifts;
    @BindView(R.id.gifts)
    LinearLayout giftsParent;


    @BindView(R.id.viewSample)
    LinearLayout mViewSample;
    @BindView(R.id.edit_sample_parent)
    LinearLayout sampleEdit;


    @BindView(R.id.area)
    TextView mArea;
    @BindView(R.id.doctor)
    TextView mDoctor;

    @BindView(R.id.remark)
    TextView mRemark;
    @BindView(R.id.withWhomText)
    TextView mWithWhomTv;
    @BindView(R.id.delete)LinearLayout mDeleteBtn;

//    private MySharedPreferences sp;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    private ReportPojo reportPojo;
    private List<ProductPojo> mProductList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        ButterKnife.bind(this);

        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);
        setToolbar();
        checkNetworkAndFetchReports();
//        parseReports();

        mViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseProductsAndShowDialog();
            }
        });

        mViewGifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseGiftAndShowDialog();
            }
        });
        mViewSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseSampleAndShowDialog();
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(reportPojo);
            }
        });


    }

    private void checkNetworkAndFetchReports() {
        if (Utils.isNetworkAvailable()) {
            getReports();
        } else {
            Utils.noInternetToast();
        }
    }

    private void parseProductsAndShowDialog() {
        String products = reportPojo.getProducts();

        try {
            mProductList.clear();
            JSONArray productsArray = new JSONArray(products);

            for (int i = 0; i < productsArray.length(); i++) {

                JSONObject object = productsArray.getJSONObject(i);
                ProductPojo pojo = new ProductPojo();
                pojo.setName(object.getString("productname") + "");
                pojo.setTotal(object.getInt("totamount") + "");
                pojo.setAmount(object.getInt("productamount") + "");
                pojo.setQty(object.getInt("productqty") + "");

                mProductList.add(pojo);
            }
            ProductsInReportAdapter adapter = new ProductsInReportAdapter(ChemistReportDetailsActivity.this, mProductList, false);
            showProductDialog("Products", adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.parsingErrorToast();
        }

    }

    private void parseGiftAndShowDialog() {
        String products = reportPojo.getGifts();

        try {
            mProductList.clear();
            JSONArray productsArray = new JSONArray(products);

            for (int i = 0; i < productsArray.length(); i++) {

                JSONObject object = productsArray.getJSONObject(i);
                ProductPojo pojo = new ProductPojo();
                pojo.setName(object.getString("giftname") + "");

                pojo.setQty(object.getInt("giftqty") + "");
                mProductList.add(pojo);
            }
            ProductsInReportAdapter adapter = new ProductsInReportAdapter(ChemistReportDetailsActivity.this, mProductList, true);
            showProductDialog("Gifts", adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.parsingErrorToast();
        }

    }


    private void showDeleteDialog(ReportPojo pojo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Report?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData(pojo);
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }


    private void deleteData(ReportPojo pojo) {
        if (Utils.isNetworkAvailable()) {
            StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.DELETE_DOCTOR_REPORT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Report Deleted!",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChemistReportDetailsActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("loginid", sp.getUserInfo(Constants.USERNAME));
                    params.put("rptid", pojo.getId());
                    return params;
                }
            };
            mRequestQue.add(request);
        }

    }


    private void parseSampleAndShowDialog() {
        String products = reportPojo.getSamples();

        try {
            mProductList.clear();
            JSONArray productsArray = new JSONArray(products);

            for (int i = 0; i < productsArray.length(); i++) {

                JSONObject object = productsArray.getJSONObject(i);
                ProductPojo pojo = new ProductPojo();
                pojo.setName(object.getString("samplename") + "");

                pojo.setQty(object.getInt("sampleqty") + "");
                mProductList.add(pojo);
            }
            ProductsInReportAdapter adapter = new ProductsInReportAdapter(ChemistReportDetailsActivity.this, mProductList, false);
            showProductDialog("Samples", adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.parsingErrorToast();
        }

    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Report Details");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbaar caolor
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void parseReports(ReportPojo reportPojo) {

        mDate.setText(reportPojo.getDate());
        mArea.setText(reportPojo.getAreaName());
        mDoctor.setText(reportPojo.getDoctorName());
        if (reportPojo.getProducts() == null || reportPojo.getProducts().isEmpty()
                || reportPojo.getProducts().equals("[]")) {
            mViewProducts.setVisibility(View.GONE);
        } else {
            changeStatusToAdded(mProductAddedText, mProductAddedImg, "Products");
        }
        if (reportPojo.getGifts() == null || reportPojo.getGifts().isEmpty()
                || reportPojo.getGifts().equals("[]")) {
            mViewGifts.setVisibility(View.GONE);
        } else {
            changeStatusToAdded(mGiftAddedText, mGiftAddedImg, "Gifts");
        }
        if (reportPojo.getSamples() == null || reportPojo.getSamples().isEmpty()
                || reportPojo.getSamples().equals("[]")) {
            mViewSample.setVisibility(View.GONE);
        } else {
            changeStatusToAdded(mSampleAddedText, mSampleAddedImg, "Samples");
        }
    }

    private void getReports() {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST,
                ENDPOINTS.GET_CHEMIST_DCR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR Chemist report onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("Data");
                        JSONObject data = dataArray.getJSONObject(0);
                        ReportPojo pojo = new ReportPojo();
                        pojo.setDoctorName(data.getString("chemistname"));
                        pojo.setAreaName(data.getString("areaname"));
                        pojo.setGifts(data.get("giftreports").toString());
                        pojo.setProducts(data.get("productreports").toString());
                        pojo.setSamples(data.get("samplereports").toString());
                        pojo.setId(data.getString("rptid"));
                        pojo.setDate(data.getString("reportdate"));
                        mWithWhomTv.setText(data.getString("withwhom"));
                        mRemark.setText(data.getString("remark"));

                        reportPojo = pojo;
                        parseReports(pojo);


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
                params.put("reportdate", sp.getUserInfo("date"));
                params.put("chemistid", sp.getUserInfo("doctor"));


                Log.d(TAG, "MUR CHEMIST REPORT getParams: " + params);
                return params;
            }
        };
        mRequestQue.add(request);
    }

    private void changeStatusToAdded(TextView tv, ImageView img, String type) {
        tv.setText(type + " Added");
        tv.setTextColor(ContextCompat.getColor(this, R.color.green));
        img.setImageDrawable(getDrawable(R.drawable.ic_right));
    }

    private void changeStatusToEmpty(TextView tv, ImageView img, String type) {
        tv.setText("No " + type + " Added");
        tv.setTextColor(ContextCompat.getColor(this, R.color.red));
        img.setImageDrawable(getDrawable(R.drawable.ic_close_red));
    }

    private void showProductDialog(String title, ProductsInReportAdapter adapter) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        View view = LayoutInflater.from(this).inflate(R.layout.show_report_dialog, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        builder.setView(view);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
