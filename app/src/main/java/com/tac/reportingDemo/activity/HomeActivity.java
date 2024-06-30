package com.tac.reportingDemo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.activity.chemist.AddChemistActivity;
import com.tac.reportingDemo.activity.chemist.ChemistBillingActivity;
import com.tac.reportingDemo.activity.chemist.SelectChemistAreaActivity;
import com.tac.reportingDemo.activity.chemist.SelectChemistAreaForReportActivity;
import com.tac.reportingDemo.adapter.AreaAdapter;
import com.tac.reportingDemo.adapter.NewsAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.AreaPojo;
import com.tac.reportingDemo.pojo.NewsPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "HomeActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.parent)
    LinearLayout mParent;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.designation)
    TextView mDesignation;
    @BindView(R.id.resign)
    TextView mResign;
    @BindView(R.id.hq)
    TextView mHq;
    @BindView(R.id.recyclerView)
    RecyclerView mNewsRecycler;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation)
    NavigationView mNavigation;
    @BindView(R.id.layoutInTime)
    LinearLayout layoutInTime;
    @BindView(R.id.layoutOutTime)
    LinearLayout layoutOutTime;
    @BindView(R.id.tvInTime)
    TextView tvInTime;
    @BindView(R.id.tvOutTime)
    TextView tvOutTime;
    @BindView(R.id.txtDeptType)
    TextView txtDeptType;
    @BindView(R.id.btnCheckIn)
    Button btnCheckIn;
    @BindView(R.id.btnCheckOut)
    Button btnCheckOut;
    private AreaAdapter adapter;
    private List<AreaPojo> mDepartmentList = new ArrayList<>();
    @BindView(R.id.spinnerArea)
    AutoCompleteTextView spinnerArea;
    @BindView(R.id.spinnerDepartment)
    Spinner spinnerDepartment;
    @BindView(R.id.btnDocList)
    ImageView btnDocList;
    @BindView(R.id.btnAreaList)
    ImageView btnAreaList;

    @BindView(R.id.spinnerDoctor)
    AutoCompleteTextView spinnerDoctor;
    ArrayAdapter areaAdapter, doctorAdapter, departmentAdapter;
    AreaPojo doctorPojo;
    AreaPojo areaPojo;
    String strDeptID = "", strDept = "", strAreaID = "", strArea = "", strDoctorID = "", strDoctor = "", strCheckinTime = "", strCheckoutTime = "", strImage = "", strDeptName;
    Boolean departmentLoaded = true, areaLoaded = true, docLoaded = true, checkinStatus = false, checkoutStatus = false;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MySharedPreferences sp;
    LocationRequest locationRequest;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FusedLocationProviderClient fusedLocationClient;
    private String lat = "0";
    String lon = "0";
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    Date date;
    private String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    };
    Comparator<AreaPojo> c = new Comparator<AreaPojo>() {
        public int compare(AreaPojo u1, AreaPojo u2) {
            return u1.getName().compareTo(u2.getName());
        }
    };

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    int REQUEST_IMAGE = 101;
    int REQUEST_CAMERA = 102;
    int REQUEST_STATE = 103;
    private List<AreaPojo> mAreaList = new ArrayList<>();
    private List<AreaPojo> mDoctorList = new ArrayList<>();


    private RequestQueue mRequestQue;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private String checkOut ="0";

    protected void createLocationRequest2() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Location currentLocation;
    private LocationCallback locationCallback;
    boolean isCheckIn = false;
    boolean isCheckOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sp = MySharedPreferences.getInstance(this);
        ButterKnife.bind(this);

        subscribeToTopic();
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Dashboard");

        mRequestQue = MyVolley.getInstance().getRequestQueue();

        showDashboardData();

        mNavigation.setItemIconTintList(null);

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mNavigation.setItemIconTintList(null);
        View view = LayoutInflater.from(this).inflate(R.layout.drawer_header,
                null,
                false);
        TextView name = view.findViewById(R.id.name);
        name.setText(sp.getUserInfo(Constants.NAME));
        TextView changePass = view.findViewById(R.id.changePass);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ChangePasswordActivity.class));
            }
        });

        mNavigation.addHeaderView(view);
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {


                    case R.id.addDoctor:
                        startActivity(new Intent(HomeActivity.this,
                                AddDoctorActivity.class));
                        break;
                    case R.id.addChemist:
                        startActivity(new Intent(HomeActivity.this,
                                AddChemistActivity.class));
                        break;
                    case R.id.myDoctor:
                        startActivity(new Intent(HomeActivity.this,
                                MyDoctorsActivity.class));
                        break;
                    case R.id.myChemist:
                        startActivity(new Intent(HomeActivity.this,
                                MyChemistActivity.class));
                        break;
                    case R.id.doctorDcr:
                        clearProductData();
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(HomeActivity.this,
                                SelectAreaActivity.class));
                        break;

                    case R.id.dcrReport:
                        clearProductData();
                        startActivity(new Intent(HomeActivity.this,
                                SelectAreaForReportActivity.class));
                        break;

                    case R.id.chemistDCRReport:

                        clearProductData();
                        startActivity(new Intent(HomeActivity.this,
                                SelectChemistAreaForReportActivity.class));
                        break;

                    case R.id.chemistDcr:
                        clearProductData();

                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(HomeActivity.this,
                                SelectChemistAreaActivity.class));
                        break;

                    case R.id.reportDesignation:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(HomeActivity.this,
                                ReportDesignationActivity.class));
                        break;

                    case R.id.missedReport:
                        clearProductData();

                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(HomeActivity.this,
                                MissedReportsActivity.class));
                        break;
                    case R.id.planTour:
                        startActivity(new Intent(HomeActivity.this, AddTourActivity.class));
                        break;
                    case R.id.tourReport:
                        startActivity(new Intent(HomeActivity.this, ViewTourReportActivity.class));
                        break;
                    case R.id.addExpense:
                        startActivity(new Intent(HomeActivity.this, AddExpenseActivity.class));
                        break;
                    case R.id.expenseReport:
                        startActivity(new Intent(HomeActivity.this, ExpenseReportActivity.class));
                        break;

//                    case R.id.doctorDcrReport:
//                        startActivity(new Intent(HomeActivity.this,
//                                DCRReportActivity.class));
//                        break;
                    case R.id.leaveApp:
                        startActivity(new Intent(HomeActivity.this, LeaveApplicationActivity.class));
                        break;
                    case R.id.logout:

                        showLogoutDialog();
                        break;
                }
                return true;
            }
        });
        Log.e(TAG, "334");
        initSideMenu();
        requestPermissions();
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isCheckIn = true;
                isCheckOut = false;
                if ((strAreaID.length() > 0) && (strDoctorID.length() > 0)&& (strDeptID.length() > 0)) {
                    updateVisitStatus("1");
                } else if (strAreaID.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please select area", Toast.LENGTH_SHORT).show();
                } else if (strDoctorID.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Please select doctor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionPopup();
//                isCheckIn = false;
//                isCheckOut = true;
//
//                if ((strAreaID.length() > 0) && (strDoctorID.length() > 0)) {
//                 captureImage(); //  updateVisitStatus("2");
//                } else if (strAreaID.length() < 1) {
//                    Toast.makeText(getApplicationContext(), "Please select area", Toast.LENGTH_SHORT).show();
//                } else if (strDoctorID.length() < 1) {
//                    Toast.makeText(getApplicationContext(), "Please select doctor", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        spinnerArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOut.equals("0")) {
                    spinnerArea.setClickable(true);
                    spinnerArea.setFocusable(true);
                    spinnerArea.setFocusableInTouchMode(true);
                    spinnerArea.showDropDown();
                }else {
                    spinnerArea.setClickable(false);
                    spinnerArea.setFocusable(false);
                    spinnerArea.setFocusableInTouchMode(false);
                }
            }
        });
        spinnerArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("value is " + ((TextView) view).getText().toString());
                int index = 0;
                for (int j = 0; j < mAreaList.size(); j++) {
                    if (mAreaList.get(j).getName().equals(((TextView) view).getText().toString())) {
                        index = j;
                        break;
                    }
                }
                if (index == 0) {
                    strArea = "";
                    strDoctorID = "";
                    areaPojo = null;
                    doctorPojo = null;
                    doctorAdapter.clear();
                } else {
                    strAreaID = mAreaList.get(index).getId();
                    areaPojo = mAreaList.get(index);
                    doctorPojo = null;
                    strArea = mAreaList.get(index).getName();
                    fetchDoctorsListData();
                }
            }
        });
/*
        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/


        Log.e(TAG, "414");
        areaAdapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_spinner_item);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOut.equals("0")) {
                    spinnerDoctor.setClickable(true);
                    spinnerDoctor.setFocusable(true);
                    spinnerDoctor.setFocusableInTouchMode(true);
                    spinnerDoctor.showDropDown();
                }else {
                    spinnerDoctor.setClickable(false);
                    spinnerDoctor.setFocusable(false);
                    spinnerDoctor.setFocusableInTouchMode(false);
                }
            }
        });
        spinnerDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = 0;
                for (int j = 0; j < mDoctorList.size(); j++) {
                    if (mDoctorList.get(j).getName().equals(((TextView) view).getText().toString())) {
                        index = j;
                        break;
                    }
                }
                if (index == 0) {
                    strDoctor = "";
                    layoutInTime.setVisibility(View.GONE);
                } else {
                    strDoctorID = mDoctorList.get(index).getId();
                    doctorPojo = mDoctorList.get(index);
                    strDoctor = mDoctorList.get(index).getName();
                    if (checkinStatus) {
                        btnCheckIn.setVisibility(View.GONE);
                    } else {
                        btnCheckIn.setVisibility(View.VISIBLE);
                    }
                }
            }
        })
        /*new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    strDoctor = "";
                    layoutInTime.setVisibility(View.GONE);
                } else {
                    strDoctorID = mDoctorList.get(position).getId();
                    doctorPojo = mDoctorList.get(position);
                    strDoctor = doctorAdapter.getItem(position).toString();
                    if (checkinStatus) {
                        btnCheckIn.setVisibility(View.GONE);
                    } else {
                        btnCheckIn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        })*/;
        departmentAdapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentAdapter.add("Select Department");
        AreaPojo p = new AreaPojo();
        p.setId("0");
        p.setName("0");
        mDepartmentList.add(p);
        p = new AreaPojo();
        p.setId("1");
        p.setName("Doctor");
        mDepartmentList.add(p);
        p = new AreaPojo();
        p.setId("2");
        p.setName("Chemist");
        mDepartmentList.add(p);
        departmentAdapter.add("Doctor");
        departmentAdapter.add("Chemist");
        spinnerDepartment.setAdapter(departmentAdapter);
        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    strDept = "";
                    layoutInTime.setVisibility(View.GONE);
                } else {
                    strDeptID = mDepartmentList.get(position).getId();
                    strDept = departmentAdapter.getItem(position).toString();
                   /* if (checkinStatus) {
                        btnCheckIn.setVisibility(View.GONE);
                    } else {
                        btnCheckIn.setVisibility(View.VISIBLE);
                    }*/
                    if (strDeptID.equals("1")) {
                        txtDeptType.setText("Doctor");
                    }
                    if (strDeptID.equals("2")) {
                        txtDeptType.setText("Chemist");
                    }
                    strArea = "";
                    strDoctorID = "";
                    areaPojo = null;
                    doctorPojo = null;
                    doctorAdapter.clear();
                    areaLoaded = true;
                    fetchAreaListData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        doctorAdapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_spinner_item);
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getLocation();
        displayLocationSettingsRequest(this);

        fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);


        if (locationCallback == null) {
            buildLocationCallback();
        }
        if (currentLocation == null) {
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
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
        btnDocList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOut.equals("0")) {
                    spinnerDoctor.setClickable(true);
                    spinnerDoctor.setFocusable(true);
                    spinnerDoctor.setFocusableInTouchMode(true);
                    spinnerDoctor.showDropDown();
                }else {
                    spinnerDoctor.setClickable(false);
                    spinnerDoctor.setFocusable(false);
                    spinnerDoctor.setFocusableInTouchMode(false);
                }
            }
        });
        btnAreaList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOut.equals("0")) {
                    spinnerArea.setClickable(true);
                    spinnerArea.setFocusable(true);
                    spinnerArea.setFocusableInTouchMode(true);
                    spinnerArea.showDropDown();
                }else {
                    spinnerArea.setClickable(false);
                    spinnerArea.setFocusable(false);
                    spinnerArea.setFocusableInTouchMode(false);
                }
            }
        });

        getCheckin();
    }

    void showSelectionPopup() {
        AlertDialog.Builder alertDialog = getBuilder();

        // set the negative button if the user is not interested to select or change already selected item
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        // create and build the AlertDialog instance with the AlertDialog builder instance
        AlertDialog customAlertDialog = alertDialog.create();

        // show the alert dialog when the button is clicked
        customAlertDialog.show();
    }

    @NonNull
    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final String[] listItems = new String[]{"Add note",
                "Take order"};

        // the function setSingleChoiceItems is the function which
        // builds the alert dialog with the single item selection
        alertDialog.setSingleChoiceItems(listItems, -1, (dialog, which) -> {
            if (which == 0) {
                Intent intent = new Intent(this, AddNotesActivity.class);
                intent.putExtra("OpenFor",strDeptID);
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                intent.putExtra("area", areaPojo.getName());
                sp.setUserInfo("areaName", areaPojo.getName());
                sp.setUserInfo("areaId", areaPojo.getId());
                sp.addGifts("");
                sp.addProduct("");
                sp.addSamples("");
                sp.setUserInfo("doctor", doctorPojo.getName());
                sp.setUserInfo("doctorId", doctorPojo.getId());
                intent.putExtra("date", outputFormat.format(date));
                sp.setUserInfo("date", outputFormat.format(date));
                startActivity(intent);
            } else if (which == 1) {
                isCheckIn = false;
                isCheckOut = true;
//                startActivity(new Intent(this, SelectAreaActivity.class));
                if (areaPojo != null && doctorPojo != null) {

                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                    Intent intent = new Intent(this, BillingActivity.class);
                    if (strDeptID.equals("2")) {
                        intent = new Intent(this, ChemistBillingActivity.class);
                    }
                    intent.putExtra("area", areaPojo.getName());
                    sp.setUserInfo("areaName", areaPojo.getName());
                    sp.setUserInfo("areaId", areaPojo.getId());
                    sp.addGifts("");
                    sp.addProduct("");
                    sp.addSamples("");
                    sp.setUserInfo("doctor", doctorPojo.getName());
                    sp.setUserInfo("doctorId", doctorPojo.getId());
                    intent.putExtra("date", outputFormat.format(date));
                    sp.setUserInfo("date", outputFormat.format(date));
                    startActivity(intent);
//                    finish();
//                    someActivityResultLauncher.launch(intent);
                }
            }
            // when selected an item the dialog should be closed with the dismiss method
            dialog.dismiss();
        });
        return alertDialog;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        if (isCheckIn) {
                            updateVisitStatus("1");
                        } else if (isCheckOut) {
                            updateVisitStatus("2");

                        }
                    }
                }
            });

    private void fetchAreaListData() {
        Log.d(TAG, "MUR AREA fetch ");
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.AREA_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);
//               Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show(); Log.d(TAG, "AREA onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");
                        areaAdapter.clear();
                        mAreaList.clear();
                        areaAdapter.add("Select Area");
                        AreaPojo p = new AreaPojo();
                        p.setId("0");
                        p.setName("0");
                        mAreaList.add(p);

                        for (int i = 0; i < dataArray.length(); i++) {
                            AreaPojo pojo = new AreaPojo();
                            JSONObject data = dataArray.getJSONObject(i);
                            pojo.setId(data.getString("AreaId"));
                            pojo.setName(data.getString("Area"));
                            areaAdapter.add(data.getString("Area"));
                            mAreaList.add(pojo);

                        }
                        spinnerArea.setAdapter(areaAdapter);
                        if (areaLoaded) {
                            getCheckin();
                            areaLoaded = false;
                        }
//                        adapter.notifyDataSetChanged();
                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                  //  Utils.makeToast("Oops! Something Went Wrong.");

                    Log.d(TAG, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR AREA error: ");
               // Utils.makeToast("Oops! Something Went Wrong.");


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

    private void fetchDoctorsListData() {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.DOCTOR_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "DOC onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");

                        doctorAdapter.clear();
                        mDoctorList.clear();
                        doctorAdapter.add("Select Doctor");

                        AreaPojo p = new AreaPojo();
                        p.setId("0");
                        p.setName("0");
                        mDoctorList.add(p);
                        for (int i = 0; i < dataArray.length(); i++) {
                            AreaPojo pojo = new AreaPojo();
                            JSONObject data = dataArray.getJSONObject(i);
                            pojo.setId(data.getString("Did"));
                            pojo.setName(data.getString("DName"));

                            mDoctorList.add(pojo);
                            doctorAdapter.add(data.getString("DName"));
                        }

                        spinnerDoctor.setAdapter(doctorAdapter);
                        if (docLoaded) {

                            int index = -1;
                            for (int a = 0; a < mDoctorList.size(); a++) {
                                if (mDoctorList.get(a).getId().equals(strDoctorID)) {
                                    index = a;
                                    break;
                                }
                            }

                            if (index > -1) {
//                                spinnerDoctor.setSelection(index);
                                doctorPojo = mDoctorList.get(index);
                                spinnerDoctor.setText(doctorPojo.getName(), false);
                            }

//                            docLoaded = false;
                        }
                        //  adapter.notifyDataSetChanged();
                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                   // Utils.makeToast("Oops! Something Went Wrong.");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR AREA error: ");
               // Utils.makeToast("Oops! Something Went Wrong.");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("city", strArea);
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("Deptid", strDeptID);
                Log.d(TAG, "MUR DOCTOR getParams: " + params);
                return params;
            }
        };

        mRequestQue.add(request);
    }


    private void subscribeToTopic() {
        if (!sp.isSubscribed()) {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLocation();

    }


    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    currentLocation = location;
                    Log.d("TAG", "onLocationResult: " + currentLocation.getLatitude());
                }
            }
        };
    }

    private void getLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                buildLocationCallback();
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        .addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                Log.d(TAG, "MUR LOC SUCCESS: ");
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d(TAG, "MUR LOC SUCCESS: NOT NuLL ");

                                    // Logic to handle location object
                                    lat = location.getLatitude() + "";
                                    lon = location.getLongitude() + "";

                                    Log.d("TAC", "MUR Lon lat home onSuccess: " + lat);
                                    if (!lat.equals("0")) {
                                        sendLocationUpdates();
                                    }
                                } else {
                                    Log.d(TAG, "MUR LOC SUCCESS: NuLL ");

//                                    getLocation();
                                }

                            }
                        });

            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "MUR LOC Failed: ");

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        Log.d(TAG, "MUR LOC Failed: TRY ");

                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                        getLocation();
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        Log.d(TAG, "MUR LOC Failed: Catch " + sendEx.getLocalizedMessage());

                    }
                }
            }
        });

    }

    private void getCheckin() {
        if (Utils.isNetworkAvailable()) {
            long timestamp = System.currentTimeMillis() / 1000;
            StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.GET_CHECKIN.trim(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject obj = new JSONObject(response);
                       // Toast.makeText(getApplicationContext(), "getCheckin " + response, Toast.LENGTH_SHORT).show();
                        if (obj.getString("message").equalsIgnoreCase("success")) {
                            JSONArray data = obj.getJSONArray("Data");
                            for (int i = 0; i < data.length(); i++) {
                            }
                            JSONObject dObj = (JSONObject) data.get(0);

                            if ((dObj.getInt("check_in") == 1) && (dObj.getInt("check_out") == 0)) {
                                checkinStatus = true;
                                strAreaID = dObj.getString("AreaId");
                                strDoctorID = dObj.getString("DocId");
                                strDeptID = dObj.getString("type");
                                Timestamp stamp = new Timestamp(dObj.getLong("locdate_In") * 1000);
                                date = new Date(stamp.getTime());

                                DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyy hh:mm:a", java.util.Locale.getDefault());
                                String out = outputFormat.format(date);

                                strCheckinTime = "" + out;

                                tvInTime.setText(strCheckinTime);
                                tvInTime.setVisibility(View.VISIBLE);
                                layoutInTime.setVisibility(View.VISIBLE);
                                btnCheckIn.setVisibility(View.GONE);
                                checkOut ="1";
                                btnCheckOut.setVisibility(View.VISIBLE);
                                tvOutTime.setVisibility(View.GONE);
                                int index = -1;
                                for (int a = 0; a < mAreaList.size(); a++) {
                                    if (mAreaList.get(a).getId().equals(strAreaID)) {
                                        index = a;
                                        break;
                                    }
                                }
                                if (index > -1 && index < mAreaList.size()) {
                                    strAreaID = mAreaList.get(index).getId();
                                    strArea = mAreaList.get(index).getName();
                                    areaPojo = mAreaList.get(index);
                                    spinnerArea.setText(strArea, false);
                                }
                                int index1 = -1;
                                for (int a = 0; a < mDepartmentList.size(); a++) {
                                    if (mDepartmentList.get(a).getId().equals(strDeptID)) {
                                        index1 = a;
                                        break;
                                    }
                                }
                                if (index1 > -1 && index1 < mDepartmentList.size()) {
                                    strDeptID = mDepartmentList.get(index1).getId();
                                    strDept = mDepartmentList.get(index1).getName();
                                    spinnerDepartment.setSelection(index1, false);
                                }
                                fetchDoctorsListData();
                                //     spinnerDoctor.setSelection(mDoctorList.indexOf(strDoctorID));
                            } else if ((dObj.getInt("check_in") == 1) && (dObj.getInt("check_out") == 1)) {
                                checkoutStatus = false;
                                checkinStatus = false;
                                layoutInTime.setVisibility(View.GONE);
                                btnCheckIn.setVisibility(View.GONE);
                                btnCheckOut.setVisibility(View.GONE);
                                spinnerArea.setText("Select Area", false);
                                spinnerDoctor.setText("Select Doctor", false);
                                strAreaID = "";
                                strDoctorID = "";

                            }
                        } else {
                            fetchDoctorsListData();
                        }


                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAC", "MUR get ERROR: " + error);

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("RId", sp.getUserInfo(Constants.R_ID));
                    Log.d(TAG, "MUR get getParams: " + params);

                    // Construct the URL with parameters
                    String urlWithParams = MyVolley.buildUrlWithParams(ENDPOINTS.GET_CHECKIN, params);
                    // Print the URL with parameters in logcat
                    Log.d("MyVolley", "getCheckIn: " + urlWithParams);
                    return params;
                }
            };
            mRequestQue.add(request);
        } else {
            Utils.noInternetToast();
        }
    }

    private void updateVisitStatus(String type) {
       // Toast.makeText(getApplicationContext(), "Update " + type, Toast.LENGTH_SHORT).show();
        String url = "";
        if (type.equalsIgnoreCase("1")) {
            url = ENDPOINTS.SEND_CHECKIN;
        } else if (type.equalsIgnoreCase("2")) {
            url = ENDPOINTS.SEND_CHECKOUT;
        }
        if (Utils.isNetworkAvailable()) {
            long timestamp = System.currentTimeMillis() / 1000;
            StringRequest request = new StringRequest(Request.Method.POST, url.trim(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("TAC", "MUR Send Loc Updates: " + response);
                    try {
                        if (JsonParsor.isReqSuccesful(response)) {
                        if (type.equalsIgnoreCase("1")) {
                            getCheckin();
                          //  Toast.makeText(getApplicationContext(), "Checkin " + response, Toast.LENGTH_SHORT).show();
                        } else if (type.equalsIgnoreCase("2")) {
                            btnCheckIn.setVisibility(View.GONE);
                            btnCheckOut.setVisibility(View.GONE);
                            layoutInTime.setVisibility(View.GONE);
                            spinnerArea.setText("Select Area",false);
                            spinnerDoctor.setText("Select Doctor",false);
                            checkoutStatus = false;
                            checkinStatus = false;
                            strAreaID = "";
                            strDoctorID = "";
                            strDeptID = "";
                            spinnerDepartment.setSelection(0);
                          //  Toast.makeText(getApplicationContext(), "Checkout " + response, Toast.LENGTH_SHORT).show();
                        }}else {
                            Utils.makeToast(JsonParsor.simpleParser(response));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAC", "MUR Send Loc ERROR: " + error);

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("latitude", lat);
                    params.put("longitude", lon);
                    params.put("locdate", timestamp + "");
                    params.put("RId", sp.getUserInfo(Constants.R_ID).trim());
                    params.put("areaid", strAreaID);
                    params.put("doctorid", strDoctorID);
                    params.put("type", strDeptID);
                    if (type.equalsIgnoreCase("1")) {
                        params.put("checkin_image", strImage);
                    } else if (type.equalsIgnoreCase("2")) {
                        params.put("checkout_image", strImage);
                    }
                    try {
                        params.put("Address", getAddressFromLocation());
                    } catch (IOException e) {
                        e.printStackTrace();
                        params.put("Address", "Can't Fetch Location");

                    }
                    Log.d(TAG, "MUR Send LOC getParams: " + strImage);
                    Log.d(TAG, "MUR Send LOC getParams: " + params);
                    return params;
                }
            };
            mRequestQue.add(request);
        } else {
            Utils.noInternetToast();
        }
    }

    private void sendLocationUpdates() {
        if (Utils.isNetworkAvailable()) {
            long timestamp = System.currentTimeMillis() / 1000;
            StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.SEND_LOCATION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("TAC", "MUR Send Loc Updates: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAC", "MUR Send Loc ERROR: " + error.networkResponse.statusCode);

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("latitude", lat);
                    params.put("longitude", lon);
                    params.put("locdate", timestamp + "");
                    params.put("RId", sp.getUserInfo(Constants.R_ID));

                    try {
                        params.put("Address", getAddressFromLocation());
                    } catch (IOException e) {
                        e.printStackTrace();
                        params.put("Address", "Can't Fetch Location");

                    }
                    Log.d(TAG, "MUR Send LOC getParams: " + params);
                    return params;
                }
            };
            mRequestQue.add(request);
        } else {
            Utils.noInternetToast();
        }
    }

    private void showDashboardData() {
        List<NewsPojo> newsList = new ArrayList<>();
        NewsAdapter adapter = new NewsAdapter(this, newsList);
        mNewsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mNewsRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mNewsRecycler.setAdapter(adapter);
        if (Utils.isNetworkAvailable()) {

            Utils.showPB(mParent, mPb);
            StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.DASHBOARD, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Utils.hidePB(mParent, mPb);
                    Log.d("TAC", "MUR DASHBOAORD RES: " + response);
                    try {
                        if (JsonParsor.isReqSuccesful(response)) {

                            JSONObject object = new JSONObject(response);
                            JSONArray dataArray = object.getJSONArray("Data");

                            JSONObject data = dataArray.getJSONObject(0);
                            mName.setText(data.getString("name"));

                            mDesignation.setText(data.getString("designation"));
                            mResign.setText(data.getString("resign"));
                            mHq.setText(data.getString("hq"));

                            JSONArray newsArray = data.getJSONArray("newsnotice");

                            for (int i = 0; i < newsArray.length(); i++) {

                                JSONObject news = newsArray.getJSONObject(i);
                                NewsPojo pojo = new NewsPojo();
                                pojo.setDate(news.getString("updatedate"));
                                pojo.setHeading(news.getString("nheading"));
                                pojo.setNews(news.getString("nallnotice"));


                                newsList.add(pojo);
                            }
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
                    Utils.hidePB(mParent, mPb);
                    Utils.parsingErrorToast();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("loginid", sp.getUserInfo(Constants.USERNAME));
                    params.put("Postid", "USER");
                    return params;
                }
            };
            mRequestQue.add(request);
        } else {
            Utils.noInternetToast();
        }
    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are You Sure?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logout();
            }
        });

        builder.show();
    }

    private void logout() {

        sp.clearData();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void selectDate() {

        Calendar fromCalender = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromCalender.set(Calendar.YEAR, year);
                fromCalender.set(Calendar.MONTH, monthOfYear);
                fromCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                fromCalender.set(Calendar.HOUR_OF_DAY, 0);
                fromCalender.set(Calendar.MINUTE, 0);
                fromCalender.set(Calendar.SECOND, 0);
//                Utils.makeToast("Selected Date is: " + dayOfMonth + " - " + monthOfYear + " - " + year);
                int month = monthOfYear + 1;

                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                if (String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + (month + 1) + "/" + year;

                }
                if (String.valueOf(month).length() <= 1) {
                    date = "" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                if (String.valueOf(month).length() <= 1 && String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                Intent intent = new Intent(HomeActivity.this, DoctorsReportActivity.class);
                sp.setReportInfo("date", date);
                intent.putExtra("date", date);

//
//                        intent.putExtra("day",dayOfMonth+"");
//                        intent.putExtra("month",monthOfYear+"");
//                        intent.putExtra("year",year+"");
                startActivity(intent);

            }


        };
        DatePickerDialog dialog = new DatePickerDialog(HomeActivity.this, fromDate, fromCalender
                .get(Calendar.YEAR), fromCalender.get(Calendar.MONTH),
                fromCalender.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
//                dialog.getDatePicker().setMinDate(1546300800000L);
        dialog.show();

    }

//    private PendingIntent getPendingIntent() {
//        Intent intent = new Intent(this, UpdateNoticationService.class);
//        intent.setAction(UpdateNoticationService.ACTION_NAME);
//        return PendingIntent.getBroadcast(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }


    private void clearProductData() {
        sp.setUserInfo("areaId", "");
        sp.setUserInfo("doctorId", "");
        sp.setUserInfo("date", "");
        sp.addGiftIds("");
        sp.addGifts("");
        sp.addProduct("");
        sp.addProductId("");
        sp.addSampleIds("");
        sp.addSamples("");

    }

    private void initSideMenu() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                permissions[0]) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    HomeActivity.this, permissions[0])
            ) {
                //Show Information about why you need the permission
                ActivityCompat.requestPermissions(HomeActivity.this,
                        permissions, PERMISSION_CALLBACK_CONSTANT);

            } else {
                //just request the permission
                ActivityCompat.requestPermissions(HomeActivity.this, permissions, PERMISSION_CALLBACK_CONSTANT);
            }


            // sp.setBoolean(permissionsRequired[0], true);

        }


    }


    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                    permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            strImage = getEncoded64ImageStringFromBitmap(imageBitmap);
            Log.e("1148", strImage);
//            Resources r = this.getResources();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos); //bm is the bitmap object
//            byte[] b = baos.toByteArray();
//String encodedImage =(Base64.encodeToString(b, Base64.DEFAULT));

//Log.e("NEWIMAGE",encodedImage);
//if(encodedImage.length()>1){
//    Toast.makeText(getApplicationContext(),"Image Captured",Toast.LENGTH_SHORT).show();
//}
//            encodedImage = Base64.encodeBytes(b);
            //  imageView.setImageBitmap(imageBitmap);
/*  March 6.03 PM
            InputStream inputStream = new FileInputStream(); // You can get an inputStream using any I/O API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            bytes = output.toByteArray();
            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

            Log.e("string64",encodedImage);
*/

//            Log.e("dev",strImage);
            if (isCheckIn) {
                updateVisitStatus("1");
            } else if (isCheckOut) {
                updateVisitStatus("2");
            }

        }

    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        final PendingResult<LocationSettingsResult> result1 = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result1.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAC", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAC", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        Log.i("TAC", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        finish();
                        break;


                }
            }
        });


    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Utils.makeToast("Please click BACK again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private String getAddressFromLocation() throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        Log.e("ADDRESS", addresses.toString() + " " + lat + " " + lon);
        String address = "";
        if (addresses.size() > 0) {
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            Log.d("TAC", "MUR getAddressFromLocation: " + address);
        }
        return address;
    }

    public void captureImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestCameraPermission();

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            } else {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }

        }
    }

    private void requestCameraPermission() {
        // Log.e("request", "Camera permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                Manifest.permission.CAMERA)) {
            // Log.e("dispalying request","Displaying camera permission rationale to provide additional context.");
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }


    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.DEFAULT);
        Toast.makeText(getApplicationContext(), "Converted", Toast.LENGTH_SHORT).show();
        return imgString;
    }
}
