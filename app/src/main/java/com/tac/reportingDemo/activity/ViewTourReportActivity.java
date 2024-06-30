


package com.tac.reportingDemo.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.tac.reportingDemo.adapter.TourReportAdapter;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.pojo.TourPojo;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewTourReportActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.parent)
    RelativeLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.no_report)
    TextView mNoReports;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @BindView(R.id.from_Date)
    Button mFrom;
    @BindView(R.id.to_Date)
    Button mTo;

    private Calendar fromCalender;
    private Calendar toCalender;
    private List<TourPojo> mList = new ArrayList<>();
    private TourReportAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_report);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);

        mRequestQue = MyVolley.getInstance().getRequestQueue();

        fromCalender = Calendar.getInstance();
        toCalender = Calendar.getInstance();

        setToolbar();

        adapter = new TourReportAdapter(this, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        checkNetworkAndFetchData();


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
    }

    private void showFromDate() {

        mTo.setText("Select To Date");
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

        dialog.getDatePicker().setMaxDate(new Date().getTime());
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
                checkNetworkAndFetchData();


            }


        };
        DatePickerDialog dialog = new DatePickerDialog(this, fromDate, toCalender
                .get(Calendar.YEAR), toCalender.get(Calendar.MONTH),
                toCalender.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(fromCalender.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//                dialog.getDatePicker().setMinDate(1546300800000L);
        dialog.show();

    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Tour Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void checkNetworkAndFetchData() {
        String fromDate = mFrom.getText().toString();
        String toDate = mTo.getText().toString();
        if (!fromDate.contains("2")) {
            Utils.makeToast("From Date Is Required!");
            return;
        }
        if (!toDate.contains("2")) {
            Utils.makeToast("To Date Is Required!");
            return;
        }
        if (Utils.isNetworkAvailable()) {
            fetchData();
        } else {
            Utils.noInternetToast();
        }
    }

    private void fetchData() {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.TOUR_REPORT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR TOUR onResponse: " + response);
                try {
                    mList.clear();

                    if (JsonParsor.isReqSuccesful(response)) {
                        Log.d(TAG, "MUR TOUR onResponse: 1 ");
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");
                        Log.d(TAG, "MUR TOUR onResponse: 2 ");


                        for (int i = 0; i < dataArray.length(); i++) {
                            TourPojo pojo = new TourPojo();
                            Log.d(TAG, "MUR TOUR onResponse: 3 ");

                            JSONObject data = dataArray.getJSONObject(i);
                            pojo.setPlace(data.getString("place"));
                            pojo.setFromDate(data.getString("fdate"));
                            Log.d(TAG, "MUR TOUR onResponse: 4 ");

                            pojo.setToDate(data.getString("tdate"));
                            pojo.setTotalDays(data.getString("totaldays"));
                            Log.d(TAG, "MUR TOUR onResponse: 5");

                            pojo.setRemark(data.getString("remark"));

                            Log.d(TAG, "MUR TOUR onResponse: 6");

                            mList.add(pojo);

                            Log.d(TAG, "MUR TOUR onResponse: 7 ");

                        }
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));
                    }
                } catch (JSONException e) {
                    Utils.makeToast("Oops! Something Went Wrong.");

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.hidePB(mParent, mPb);
                Log.d(TAG, "MUR AREA error: ");
                Utils.makeToast("Oops! Something Went Wrong.");


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("fromdate", mFrom.getText().toString());
                params.put("todate", mTo.getText().toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        mRequestQue.add(request);
    }

}
