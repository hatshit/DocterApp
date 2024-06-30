package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddExpenseActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.parent)
    ScrollView mParent;
    @BindView(R.id.placeFrom)
    EditText mPlaceFrom;
    @BindView(R.id.placeTo)
    EditText mPlaceTo;
    @BindView(R.id.mode)
    EditText mMode;
    @BindView(R.id.workDate)
    EditText mWorkDate;
    @BindView(R.id.allowance)
    EditText mAllowance;
    @BindView(R.id.totalAmount)
    EditText mTotalAmount;
    @BindView(R.id.remark)
    EditText mRemark;
    @BindView(R.id.misamount)
    EditText mMissAmount;
    @BindView(R.id.submit)
    Button mAddExpenseBtn;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        ButterKnife.bind(this);

        setToolbar();

        sp = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        mAllowance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int allowance = Integer.parseInt(mAllowance.getText().toString());
                    int missAmount = Integer.parseInt(mMissAmount.getText().toString());

                    int total = allowance + missAmount;
                    mTotalAmount.setText(total);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mMissAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAddExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndAddExpence();
            }
        });

        mWorkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

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

                String date = dayOfMonth + "/" + (month ) + "/" + year;
                if (String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + (month ) + "/" + year;

                }
                if (String.valueOf(month).length() <= 1) {
                    date = "" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                if (String.valueOf(month).length() <= 1 && String.valueOf(dayOfMonth).length() <= 1) {
                    date = "0" + dayOfMonth + "/" + "0" + month + "/" + year;

                }

                mWorkDate.setText(date);

            }


        };
        DatePickerDialog dialog = new DatePickerDialog(AddExpenseActivity.this, fromDate, fromCalender
                .get(Calendar.YEAR), fromCalender.get(Calendar.MONTH),
                fromCalender.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
//                dialog.getDatePicker().setMinDate(1546300800000L);
        dialog.show();

    }


    private void calculateTotal() {
        try {
            String missAmountStr = (mMissAmount.getText().toString());
            String allowanceStr = (mAllowance.getText().toString());

            if (!missAmountStr.isEmpty() && !allowanceStr.isEmpty()) {
                int allowance = Integer.parseInt(mAllowance.getText().toString());
                int missAmount = Integer.parseInt(mMissAmount.getText().toString());

                int total = allowance + missAmount;
                Log.d(TAG, "MUR TOTAL AMOUNT IS: " + total);
                mTotalAmount.setText(total + "");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void checkDataAndAddExpence() {
        String placeFrom = mPlaceFrom.getText().toString().trim();
        String placeTo = mPlaceTo.getText().toString().trim();
        String workDate = mWorkDate.getText().toString().trim();
        String allowance = mAllowance.getText().toString().trim();
        String missAmount = mMissAmount.getText().toString().trim();
        String totalAmount = mTotalAmount.getText().toString().trim();
        String mode = mMode.getText().toString().trim();
        String remark = mRemark.getText().toString().trim();

        if (placeFrom.isEmpty()) {
            setError(mPlaceFrom);
            return;
        }
        if (placeTo.isEmpty()) {
            setError(mPlaceTo);
            return;
        }
        if (workDate.isEmpty()) {
            setError(mWorkDate);
            return;
        }

        if (mode.isEmpty()) {
            setError(mWorkDate);
            return;
        }
        if (allowance.isEmpty()) {
            setError(mAllowance);
            return;
        }

        if (missAmount.isEmpty()) {
            setError(mMissAmount);
            return;
        }


        checkNetworkAndAddExpense(placeFrom, placeTo, workDate, allowance, missAmount, mode, totalAmount, remark);
    }

    private void checkNetworkAndAddExpense(String placeFrom, String placeTo, String workDate, String allowance, String missAmount, String mode, String totalAmount, String remark) {
        if (Utils.isNetworkAvailable()) {

            addExpense(placeFrom, placeTo, workDate, allowance, missAmount, mode, remark, totalAmount);
        } else {
            Utils.noInternetToast();
        }
    }

    private void addExpense(String placeFrom, String placeTo, String workDate, String allowance, String missAmount, String mode, String remark, String totalAmount) {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.ADD_EXPENSE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "MUR ADD EXPENSE RESPONSE: " + response);
                Utils.hidePB(mParent, mPb);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        Utils.makeToast("Expense Added!");
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
                Log.d(TAG, "MUR ADD EXPENSE Error: " + error.networkResponse.statusCode);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("workdate", workDate);
                params.put("placefrom", placeFrom);
                params.put("placeto", placeTo);
                params.put("mode", mode);
                params.put("remark", remark);
                params.put("totalamount", totalAmount);
                params.put("misamount", missAmount);
                params.put("allowance", allowance);
                params.put("Rid", sp.getUserInfo(Constants.R_ID));
                params.put("fare", "");
                params.put("distance", "");
                params.put("grandtotal", missAmount);
                params.put("deduction", "");
                Log.d(TAG, "MUR ADD EXPENSE Params: " + params);
                return params;
            }
        };
        mRequestQue.add(request);

    }

    private void setError(EditText editText) {
        editText.setError(" Field Is Mandatory");

    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Add Expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
