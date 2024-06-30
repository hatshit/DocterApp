package com.tac.reportingDemo.activity.chemist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.AddGiftActivity;
import com.tac.reportingDemo.activity.AddProductsActivity;
import com.tac.reportingDemo.activity.AddSampleActivity;
import com.tac.reportingDemo.activity.BillingActivity;
import com.tac.reportingDemo.activity.EditGiftActivity;
import com.tac.reportingDemo.activity.EditProductActivity;
import com.tac.reportingDemo.activity.EditSampleActivity;
import com.tac.reportingDemo.activity.HomeActivity;
import com.tac.reportingDemo.adapter.ShowAddedGiftsAdapter;
import com.tac.reportingDemo.adapter.ShowAddedProductsAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.ProductPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChemistBillingActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "TAC";
    private static final int REQUEST_CHECK_SETTINGS = 203;

    @BindView(R.id.parent)
    RelativeLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.submit)
    Button mSumbitBtn;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.add_products)
    LinearLayout mAddProducts;
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


    @BindView(R.id.add_gifts)
    LinearLayout mAddGifts;
    @BindView(R.id.gifts)
    LinearLayout giftsParent;


    @BindView(R.id.add_samples)
    LinearLayout mAddSample;
    @BindView(R.id.edit_sample_parent)
    LinearLayout sampleEdit;


    @BindView(R.id.area)
    TextView mArea;
    @BindView(R.id.doctor)
    TextView mDoctor;

    @BindView(R.id.remark)
    EditText mRemark;
    @BindView(R.id.withWhom)
    LinearLayout mWhom;

    @BindView(R.id.withWhomText)
    TextView mWithWhomTv;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private List<ProductPojo> productList = new ArrayList<>();
    private List<ProductPojo> giftList = new ArrayList<>();
    private List<ProductPojo> sampleList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private String lat = "0";
    String lon = "0";
    private String date = "";
    private String whom = "Alone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        ButterKnife.bind(this);

        Log.d(TAG, "MUR ON CREATE:  BIllING FOR CHEMIST DCR ");

        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);
        setToolbar();
        mSumbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndSubmit();
            }
        });

        sp.setBillingType("chemist");
        date = sp.getUserInfo("date");
        mDate.setText(date);
        mArea.setText(sp.getUserInfo("areaName"));
        mDoctor.setText(sp.getUserInfo("doctor"));

        fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);


        showHideViews();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            lat = location.getLatitude() + "";
                            lon = location.getLongitude() + "";
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "MUR LOC FAILURE : " + e.getMessage());
                    }
                });

        mAddProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.addProduct("");
                startActivity(new Intent(ChemistBillingActivity.this,
                        AddProductsActivity.class));
            }
        });

//        mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAllAddedProducts();
//            }
//        });

//        mRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sp.addProduct("");
//                productParent.setVisibility(View.GONE);
//                mAddProducts.setVisibility(View.VISIBLE);
//                mProductsAdded.setVisibility(View.GONE);
//            }
//        });

        mAddGifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.addGifts("");
                startActivity(new Intent(ChemistBillingActivity.this, AddGiftActivity.class));
            }
        });

//        mGiftView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAllAddedGifts();
//            }
//        });

//        mGiftRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sp.addGifts("");
//                giftsParent.setVisibility(View.GONE);
//                mAddGifts.setVisibility(View.VISIBLE);
//            }
//        });

        mAddSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.addSamples("");
                startActivity(new Intent(ChemistBillingActivity.this, AddSampleActivity.class));
            }
        });

//        mSampleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAllAddedSamples();
//            }
//        });

//        mSampleRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sp.addSamples("");
//                sampleEdit.setVisibility(View.GONE);
//                mAddSample.setVisibility(View.VISIBLE);
//            }
//        });

        productParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChemistBillingActivity.this, EditProductActivity.class));
            }
        });
        giftsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChemistBillingActivity.this, EditGiftActivity.class));
            }
        });

        sampleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChemistBillingActivity.this, EditSampleActivity.class));

            }
        });
        mWhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWhomDialog();
            }
        });

    }

    private void showHideViews() {
        if (!sp.getProduct().equals("")) {
            mAddProducts.setVisibility(View.GONE);
            productParent.setVisibility(View.VISIBLE);
//            mProductsAdded.setVisibility(View.VISIBLE);
            changeStatusToAdded(mProductAddedText, mProductAddedImg, " Product");

            getProducts();
            parseProducts();
        }
        if (!sp.getGifts().equals("")) {
            Log.d(TAG, "MUR GIFT HAS DATA: " + sp.getGifts());
            mAddGifts.setVisibility(View.GONE);
            giftsParent.setVisibility(View.VISIBLE);
            changeStatusToAdded(mGiftAddedText, mGiftAddedImg, " Gifts");
            getGifts();
            parseGifts();
        }
        if (!sp.getSamples().equals("")) {
            Log.d(TAG, "MUR Sample HAS DATA: ");

            mAddSample.setVisibility(View.GONE);
//            sampleEdit.setVisibility(View.VISIBLE);
            getSamples();
            parseSample();
            changeStatusToAdded(mSampleAddedText, mSampleAddedImg, " Samples");
            sampleEdit.setVisibility(View.VISIBLE);
        }

    }


    private void checkDataAndSubmit() {
//        if (giftList.isEmpty() && productList.isEmpty() && sampleList.isEmpty()) {
//            Utils.makeToast("Please add products or gifts");
//            return;
//        }

        if (lat.equals("0")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                lat = location.getLatitude() + "";
                                lon = location.getLongitude() + "";
                                checkNetworkAndSubmit();
                            } else {
                                Utils.makeToast("Failed To Fetch Location, Please Turn on GPS");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "MUR LOC FAILURE : " + e.getMessage());
                            Utils.makeToast("Can't Fetch Location, Please Check Your Location Settings!");

                        }
                    });
        } else {
            checkNetworkAndSubmit();
        }
    }

    private void checkNetworkAndSubmit() {
        if (Utils.isNetworkAvailable()) {

            submit();
        } else {
            Utils.noInternetToast();
        }
    }

    private void submit() {
        Utils.showPB(mParent, mPb);
        String remark = mRemark.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.SUBMIT_CHEMIST_DCR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "MUR SUBMIT RES: " + response);
                        Utils.hidePB(mParent, mPb);
                        try {
                            if (JsonParsor.isReqSuccesful(response)) {
                                Utils.makeToast("Report Submitted");
                                Intent intent = new Intent(ChemistBillingActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
//                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {
                                Utils.makeToast(JsonParsor.simpleParser(response));
                                Intent intent = new Intent(ChemistBillingActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utils.hidePB(mParent, mPb);
                Utils.makeToast("oops something went wrong!");
            }
        }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                String product = "";
                if (!productList.isEmpty()) {
                    for (int i = 0; i < productList.size(); i++) {

                        if (i != 0) {
                            ProductPojo pojo = productList.get(i);
                            product = product + "|" + pojo.getId() + "," + pojo.getQty() + "," + pojo.getAmount() + "," + pojo.getTotal();
                        } else {
                            ProductPojo pojo = productList.get(i);
                            product = pojo.getId() + "," + pojo.getQty() + "," + pojo.getAmount() + "," + pojo.getTotal();

                        }
                    }
                }
                String gift = "";
                if (!giftList.isEmpty()) {
                    for (int i = 0; i < giftList.size(); i++) {

                        if (i != 0) {
                            ProductPojo pojo = giftList.get(i);
                            gift = gift + "|" + pojo.getId() + "," + pojo.getQty();
                        } else {
                            ProductPojo pojo = giftList.get(i);
                            gift = pojo.getId() + "," + pojo.getQty();

                        }
                    }
                }

                String sample = "";
                if (!sampleList.isEmpty()) {
                    for (int i = 0; i < sampleList.size(); i++) {

                        if (i != 0) {
                            ProductPojo pojo = sampleList.get(i);
                            sample = gift + "|" + pojo.getId() + "," + pojo.getQty();
                        } else {
                            ProductPojo pojo = sampleList.get(i);
                            sample = pojo.getId() + "," + pojo.getQty();

                        }
                    }
                }

                params.put("product_Items", product);
                params.put("gift_Items", gift);
                params.put("sample_Items", sample);
                params.put("latitude", lat);
                params.put("longitude", lon);
                params.put("areaid", sp.getUserInfo("areaId"));
                params.put("chemistid", sp.getUserInfo("doctorId"));
                params.put("RId", sp.getUserInfo(Constants.R_ID));
                params.put("loginid", sp.getUserInfo(Constants.USERNAME));
                params.put("reportdate", date);
                params.put("remark", remark);
                params.put("withwhom",whom);
                params.put("type", "2");
                try {
                    params.put("Address", getAddressFromLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                    params.put("Address", "Can't Fetch Address");
                }
                Log.d(TAG, "MUR DCR SUBMIT Params: " + params);


                return params;
            }

        };
        mRequestQue.add(request);
    }

    private void showAllAddedProducts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Products");

        View view = LayoutInflater.from(this).inflate(R.layout.show_all_product_dialog, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShowAddedProductsAdapter adapter = new ShowAddedProductsAdapter(this, productList);

        String products = sp.getProduct();


        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);

        builder.show();


    }

    private void showAllAddedGifts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Gifts");

        View view = LayoutInflater.from(this).inflate(R.layout.show_all_gifts_dialog, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShowAddedGiftsAdapter adapter = new ShowAddedGiftsAdapter(this, giftList);


        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);

        builder.show();


    }

    private void showAllAddedSamples() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Samples");

        View view = LayoutInflater.from(this).inflate(R.layout.show_all_gifts_dialog, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShowAddedGiftsAdapter adapter = new ShowAddedGiftsAdapter(this, sampleList);


        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);

        builder.show();


    }


    private void getProducts() {

        String products = sp.getProduct();
        Log.d(TAG, "MUR PRODUCTS LIST : " + products);
        try {
            JSONObject parentObj = new JSONObject(products);
            Gson gson = new Gson();
            List<String> productIds = gson.fromJson(sp.getProductId(), ArrayList.class);
            for (int i = 0; i < productIds.size(); i++) {
                JSONObject object = parentObj.getJSONObject(productIds.get(i));
                ProductPojo pojo = new ProductPojo();
                pojo.setName(object.getString("name"));
                pojo.setAmount(object.getString("amount"));
                pojo.setQty(object.getString("qty"));
                pojo.setId(object.getString("id"));
                pojo.setTotal(object.getString("total"));
                productList.add(pojo);
            }

        } catch (JSONException ignored) {

        }
    }

    private void parseProducts() {
        String products = sp.getProduct();

        try {
            JSONArray array = new JSONArray(products);
            JSONObject object = array.getJSONObject(0);
//            mName.setText(object.getString("name"));
//            mQty.setText(object.getString("qty"));
//            mAmount.setText(object.getString("amount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getGifts() {

        String products = sp.getGifts();
        try {
            JSONObject parentObj = new JSONObject(products);
            Gson gson = new Gson();
            List<String> productIds = gson.fromJson(sp.getGiftIds(), ArrayList.class);
            for (int i = 0; i < productIds.size(); i++) {
                JSONObject object = parentObj.getJSONObject(productIds.get(i));
                ProductPojo pojo = new ProductPojo();
                pojo.setName(object.getString("name"));
//                pojo.setAmount(object.getString("amount"));
                pojo.setQty(object.getString("qty"));
                pojo.setId(object.getString("id"));

                giftList.add(pojo);
            }

        } catch (JSONException e) {

        }
    }

    private void parseGifts() {
        String products = sp.getGifts();

        Log.d(TAG, "MUR GIFTS LIST : " + sp.getGifts());
        try {
            JSONArray array = new JSONArray(products);
            JSONObject object = array.getJSONObject(0);
//            mGiftName.setText(object.getString("name"));
//            mGiftQty.setText(object.getString("qty"));

//            mAmount.setText(object.getString("amount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSamples() {

        String products = sp.getSamples();
        try {
            JSONObject parentObj = new JSONObject(products);
            Gson gson = new Gson();
            List<String> productIds = gson.fromJson(sp.getSampleIds(), ArrayList.class);
            for (int i = 0; i < productIds.size(); i++) {
                JSONObject object = parentObj.getJSONObject(productIds.get(i));
                ProductPojo pojo = new ProductPojo();
                pojo.setName(object.getString("name"));
//                pojo.setAmount(object.getString("amount"));
                pojo.setQty(object.getString("qty"));
                pojo.setId(object.getString("id"));

                sampleList.add(pojo);
            }

        } catch (JSONException e) {

        }
    }

    private void parseSample() {
        String products = sp.getSamples();

        Log.d(TAG, "MUR GIFTS LIST : " + sampleList);
        try {
            JSONArray array = new JSONArray(products);
            JSONObject object = array.getJSONObject(0);
//            mSampleName.setText(object.getString("name"));
//            mSampleQty.setText(object.getString("qty"));
//            mAmount.setText(object.getString("amount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("File DCR");
//        getSupportActionBar().setTitle("Doctor DCR");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelectChemistActivity.class);
        startActivity(intent);
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

    private String getAddressFromLocation() throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        Log.d(TAG, "MUR getAddressFromLocation: " + address);
        return address;


    }

    private void showWhomDialog() {
        List<String> whomList = new ArrayList<>();
        whomList.add("Alone");
        whomList.add("ASM");
        whomList.add("Business Head");
        whomList.add("ZM");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,whomList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You Are Reporting With");
        View view = LayoutInflater.from(this).inflate(R.layout.list_alert_view, null, false);

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                whom = whomList.get(position);
                mWithWhomTv.setText(whom);

                dialog.dismiss();

            }
        });


    }

}
