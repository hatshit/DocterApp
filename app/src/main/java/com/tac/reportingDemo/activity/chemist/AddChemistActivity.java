package com.tac.reportingDemo.activity.chemist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.AddDoctorActivity;
import com.tac.reportingDemo.activity.MapsActivity;
import com.tac.reportingDemo.adapter.AreaAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.AddDoctorPojo;
import com.tac.reportingDemo.pojo.AreaPojo;
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

public class AddChemistActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.parent)
    LinearLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.name)
    EditText mName;
    @BindView(R.id.mobile)
    EditText mMobile;
    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.firmName)
    EditText mFirmName;

    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.locationMarker)
    ImageView locationMarker;
    @BindView(R.id.area)
    EditText mArea;
    @BindView(R.id.state)
    EditText mState;

    @BindView(R.id.submit)
    Button mSubmit;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @BindView(R.id.addArea)
    ImageView mAddArea;

    @BindView(R.id.city)
    EditText mCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chemist);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        setToolbar();
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndAddDoctor();
            }
        });
        mArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreaDialog();
                mArea.setError(null);
            }
        });
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddress.setError(null);
            }
        });

        locationMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
                mAddress.setError(null);
            }
        });


        mAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAreaDialog();
            }
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        if (data.getStringExtra("Address") != null) {
                            String address = data.getStringExtra("Address");
                            mAddress.setText(address);
                            if (address != null) {
                                HashMap<String, String> addressDetails = getAddressDetails(address);
                               mCity.setText(addressDetails.get("city"));
                                mState.setText(addressDetails.get("state"));
                            }
                        }
                    }
                }
            });

    private void showMap() {
        Intent intent = new Intent(AddChemistActivity.this, MapsActivity.class);
        someActivityResultLauncher.launch(intent);

      /*
        startActivity(intent);*/
    }

    private void checkDataAndAddDoctor() {
        AddDoctorPojo pojo = new AddDoctorPojo();
        pojo.setName(mName.getText().toString());
        pojo.setMobile(mMobile.getText().toString());
        pojo.setEmail(mEmail.getText().toString());
        pojo.setHospitalName(mFirmName.getText().toString());
        pojo.setAddress(mAddress.getText().toString());
        pojo.setArea(mArea.getText().toString());
        pojo.setCity(mCity.getText().toString());
        pojo.setState(mState.getText().toString());

        if (pojo.getName().isEmpty()) {
            mName.setError("Field Is Mandatory");
            return;
        }

        if (pojo.getMobile().isEmpty()) {
            mMobile.setError("Field Is Mandatory");
            return;
        }

        if (pojo.getArea().isEmpty()) {
            mArea.setError("Field Is Mandatory");
            return;
        }

        if (pojo.getAddress().isEmpty()) {
            mAddress.setError("Field Is Mandatory");
            return;
        }

        checkNetworkAndAddChemist(pojo);
    }

    private void checkNetworkAndAddChemist(AddDoctorPojo pojo) {
        if (Utils.isNetworkAvailable()) {
            addChemist(pojo);
        } else {
            Utils.noInternetToast();
        }
    }

    private void addChemist(AddDoctorPojo pojo) {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.ADD_CHEMIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.hidePB(mParent, mPb);
                        try {
                            if (JsonParsor.isReqSuccesful(response)) {
                                Utils.makeToast("Chemist Added Successfully!");
                                onBackPressed();

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

                Utils.hidePB(mParent, mPb);
                Utils.parsingErrorToast();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("status", "1");
                params.put("chemistname", pojo.getName());
                params.put("FirmName", pojo.getHospitalName());
                params.put("MobileNo", pojo.getMobile());
                params.put("EmailId", pojo.getEmail());
                params.put("Address", pojo.getAddress());
                params.put("City", pojo.getArea());
                params.put("State", pojo.getState());
                params.put("Area", pojo.getCity());
                return params;
            }
        };
        mRequestQue.add(request);
    }


    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Add Chemist");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change the color of the back button
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private AlertDialog dialog;

    private void showAreaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select City");

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        View view = LayoutInflater.from(this).inflate(R.layout.show_report_dialog, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ProgressBar pb = view.findViewById(R.id.pb);
        getAreas(recyclerView, pb);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    private void getAreas(RecyclerView recyclerView, ProgressBar pb) {
        Utils.showPB(recyclerView, pb);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        List<AreaPojo> areaList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.AREA_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(recyclerView, pb);
                Log.d(TAG, "MUR AREA onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");

                        areaList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            AreaPojo pojo = new AreaPojo();
                            JSONObject data = dataArray.getJSONObject(i);
                            pojo.setId(data.getString("AreaId"));
                            pojo.setName(data.getString("Area"));

                            areaList.add(pojo);

                        }
                        AreaAdapter adapter = new AreaAdapter(AddChemistActivity.this, areaList, 5);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        dialog.dismiss();
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                    Utils.makeToast("Oops! Something Went Wrong.");
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(recyclerView, pb);
                dialog.dismiss();
                Log.d(TAG, "MUR AREA error: ");
                Utils.makeToast("Oops! Something Went Wrong.");


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        mRequestQue.add(request);
    }

    public void setArea(String area) {
        dialog.dismiss();
        mArea.setText(area);
    }

    public HashMap<String, String> getAddressDetails(String addressString) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        HashMap<String, String> addressDetailsMap = new HashMap<>();

        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();

                // Put the details in the HashMap
                addressDetailsMap.put("city", city);
                addressDetailsMap.put("state", state);
                addressDetailsMap.put("country", country);

                return addressDetailsMap;
            } else {
                // Handle case where no address was found
                System.out.println("No address found for the given location.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
            return null;
        }
    }

    private void showAddAreaDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.show_add_area_dialog, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Initialize the EditText and Buttons
        final EditText editTextInput = dialogView.findViewById(R.id.edtAddArea);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        RelativeLayout RRCross=dialogView.findViewById(R.id.RRCross);
        Button buttonCross=dialogView.findViewById(R.id.buttonCross);
        // Create the AlertDialog
        final AlertDialog alertDialog = builder.create();

        // Set the button click listeners
        RRCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                alertDialog.dismiss();
            }
        });

        // Set the button click listeners
        buttonCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                alertDialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input text
                String area = editTextInput.getText().toString().trim();
                if(area.equals("")) {
                    editTextInput.setError("Field Is Mandatory");
                }else {
                    addAreApi(area);
                    alertDialog.dismiss();
                }
                // Close the dialog

            }
        });

        // Show the dialog
        alertDialog.show();
    }

    private void addAreApi(String area) {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.ADD_AREA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.hidePB(mParent, mPb);
                        try {
                            if (JsonParsor.isReqSuccesful(response)) {
                                Utils.makeToast("Area Added Successfully!");
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

                Utils.hidePB(mParent, mPb);
                Utils.parsingErrorToast();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("HQid", sp.getUserInfo(Constants.HQ_id));
                params.put("Area", area);
                return params;
            }
        };
        mRequestQue.add(request);
    }
}
