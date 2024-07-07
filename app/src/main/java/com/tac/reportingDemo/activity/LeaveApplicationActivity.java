package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaveApplicationActivity extends AppCompatActivity {


    private static final String TAG = "LeaveApplication";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.parent)
    ScrollView mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;

    @BindView(R.id.from_Date)
    EditText mFrom;
    @BindView(R.id.to_Date)
    EditText mTo;
    @BindView(R.id.submit)
    Button mSubmit;
    @BindView(R.id.reason)
    EditText mReason;

    private Calendar fromCalender;
    private Calendar toCalender;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);
        ButterKnife.bind(this);
        setToolbar();
        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        fromCalender = Calendar.getInstance();
        toCalender = Calendar.getInstance();
        mFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDate();
            }
        });
        mTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromDate = mFrom.getText().toString().trim();
                if (fromDate.isEmpty()) {
                    Utils.makeToast("Please Select From Date First");
                } else {
                    showToDate();
                }
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndSubmitTour();
            }
        });

    }

    private void showFromDate() {

        mTo.setText("");
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

                String date = dayOfMonth + "/" + (month) + "/" + year;
                if (String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + (month) + "/" + year;

                }
                if (String.valueOf(month).length() <= 1) {
                    date = "" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                if (String.valueOf(month).length() <= 1 && String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                mFrom.setText(date);


            }


        };
        DatePickerDialog dialog = new DatePickerDialog(this, fromDate, fromCalender
                .get(Calendar.YEAR), fromCalender.get(Calendar.MONTH),
                fromCalender.get(Calendar.DAY_OF_MONTH));
//        dialog.getDatePicker().setMaxDate(new Date().getTime());

//        dialog.getDatePicker().setMaxDate(new Date().getTime());
//        dialog.getDatePicker().setMinDate(fromCalender.getTimeInMillis());
//                dialog.getDatePicker().setMinDate(1546300800000L);
        dialog.show();

    }


    private void showToDate() {

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toCalender.set(Calendar.YEAR, year);
                toCalender.set(Calendar.MONTH, monthOfYear);
                toCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                toCalender.set(Calendar.HOUR_OF_DAY, 0);
                toCalender.set(Calendar.MINUTE, 0);
                toCalender.set(Calendar.SECOND, 0);
//                Utils.makeToast("Selected Date is: " + dayOfMonth + " - " + monthOfYear + " - " + year);
                int month = monthOfYear + 1;

                String date = dayOfMonth + "/" + (month ) + "/" + year;
                if (String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + (month) + "/" + year;

                }
                if (String.valueOf(month).length() <= 1) {
                    date = "" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                if (String.valueOf(month).length() <= 1 && String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                mTo.setText(date);


            }


        };
        DatePickerDialog dialog = new DatePickerDialog(this, fromDate, toCalender
                .get(Calendar.YEAR), toCalender.get(Calendar.MONTH),
                toCalender.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(fromCalender.getTimeInMillis());
//                dialog.getDatePicker().setMinDate(1546300800000L);
        dialog.show();

    }

    private void checkDataAndSubmitTour() {
        String toDate, fromDate, reason;
        toDate = mTo.getText().toString().trim();
        fromDate = mFrom.getText().toString().trim();
        reason = mReason.getText().toString().trim();


        if (fromDate.isEmpty()) {
            Utils.makeToast("From Date is Required");
            return;
        }

        if (toDate.isEmpty()) {
            Utils.makeToast("To Date is Required");
            return;
        }
        if (reason.isEmpty()) {
            Utils.makeToast("Place is Required");
            return;
        }
        checkNetworkAndSubmit(fromDate, toDate, reason);

    }

    private void checkNetworkAndSubmit(String fromDate, String toDate, String reason) {
        if (Utils.isNetworkAvailable()) {
            submitLeaveApplication(fromDate, toDate, reason);

        } else {
            Utils.noInternetToast();
        }
    }

    private void submitLeaveApplication(String fromDate, String toDate, String reason) {
        Utils.showPB(mParent, mPb);

        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.LEAVE_APPLICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);

                Log.d(TAG, "MUR LEAVE onResponse: "+response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        Utils.makeToast("Leave Application Submitted");
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
                Log.d(TAG, "MUR LEAVE error: "+error.networkResponse.statusCode);

                Utils.hidePB(mParent, mPb);
                Utils.parsingErrorToast();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("RId", sp.getUserInfo(Constants.R_ID));
                params.put("todate", toDate);
                params.put("fromdate", fromDate);
                params.put("reason", reason);

                Log.d(TAG, "MUR LEAVE PARAMS: "+params);
                return params;
            }
        };

        mRequestQue.add(request);
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Leave Application");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//toolbaar caolor
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

    }
}
