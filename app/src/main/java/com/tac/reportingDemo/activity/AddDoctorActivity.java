package com.tac.reportingDemo.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddDoctorActivity extends AppCompatActivity {

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
    @BindView(R.id.hospital)
    EditText mHospitalName;
    @BindView(R.id.dob)
    EditText mDOB;
    @BindView(R.id.qualification)
    EditText mQualification;
    @BindView(R.id.specialist)
    EditText mSpeciality;
    @BindView(R.id.address)
    EditText mAddress;
    @BindView(R.id.locationMarker)
    ImageView locationMarker;
    @BindView(R.id.area)
    EditText mArea;
    @BindView(R.id.city)
    EditText mCity;

    @BindView(R.id.state)
    EditText mState;
    @BindView(R.id.country)
    EditText mCountry;
    @BindView(R.id.addArea)
    ImageView mAddArea;

    @BindView(R.id.submit)
    Button mSubmit;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        setToolbar();
        mDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showAreaDialog();
                mArea.setError(null);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndAddDoctor();
            }
        });

        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddress.setError(null);
            }
        });

        mAddArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showAddAreaDialog();
            }
        });

        locationMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
                mAddress.setError(null);
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
                                mCountry.setText(addressDetails.get("country"));
                            }
                        }
                    }
                }
            });

    private void showMap() {
        Intent intent = new Intent(AddDoctorActivity.this, MapsActivity.class);
        someActivityResultLauncher.launch(intent);

      /*
        startActivity(intent);*/
    }

    private void checkDataAndAddDoctor() {
        AddDoctorPojo pojo = new AddDoctorPojo();
        pojo.setName(mName.getText().toString());
        pojo.setMobile(mMobile.getText().toString());
        pojo.setEmail(mEmail.getText().toString());
        pojo.setDob(mDOB.getText().toString());
        pojo.setSpeciality(mSpeciality.getText().toString());
        pojo.setQualification(mQualification.getText().toString());
        pojo.setHospitalName(mHospitalName.getText().toString());
        pojo.setAddress(mAddress.getText().toString());
        pojo.setArea(mArea.getText().toString());
        pojo.setCity(mCity.getText().toString());
        pojo.setState(mState.getText().toString());
        pojo.setCountry(mCountry.getText().toString());

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


        checkNetworkAndAddDoctor(pojo);
    }

    private void checkNetworkAndAddDoctor(AddDoctorPojo pojo) {
        if (Utils.isNetworkAvailable()) {
            addDoctor(pojo);
        } else {
            Utils.noInternetToast();
        }
    }

    private void addDoctor(AddDoctorPojo pojo) {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.ADD_DOCTOR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.hidePB(mParent, mPb);
                        try {
                            if (JsonParsor.isReqSuccesful(response)) {
                                Utils.makeToast("Doctor Added Successfully!");
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
                params.put("doctorname", pojo.getName());
                params.put("HospitalName", pojo.getHospitalName());
                params.put("MobileNo", pojo.getMobile());
                params.put("EmailId", pojo.getEmail());
                params.put("Dob", pojo.getDob());
                params.put("Address", pojo.getAddress());
                params.put("City", pojo.getArea());
                params.put("State", pojo.getState());
                params.put("Country", pojo.getCity());
                params.put("Specilist", pojo.getSpeciality());
                params.put("Qualification", pojo.getQualification());
                params.put("Area", pojo.getCity());
                return params;
            }
        };
        mRequestQue.add(request);
    }

    private void showDatePicker() {
        Calendar fromCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the calendar to the selected date
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, monthOfYear);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
                fromCalendar.set(Calendar.MINUTE, 0);
                fromCalendar.set(Calendar.SECOND, 0);

                // Format the date
                int month = monthOfYear + 1;
                String dayStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String monthStr = month < 10 ? "0" + month : String.valueOf(month);
                String date = dayStr + "/" + monthStr + "/" + year;

                // Set the formatted date to the AutoCompleteTextView
                mDOB.setText(date);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(this, fromDate,
                fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                fromCalendar.get(Calendar.DAY_OF_MONTH));

        // Set maximum date to January 1, 2000
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2000, Calendar.JANUARY, 1);
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        dialog.show();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Add Doctor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    AlertDialog dialog;

    private void showAreaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Area");

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
                        AreaAdapter adapter = new AreaAdapter(AddDoctorActivity.this, areaList, 4);
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


    //this method used to address to all details get..
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
