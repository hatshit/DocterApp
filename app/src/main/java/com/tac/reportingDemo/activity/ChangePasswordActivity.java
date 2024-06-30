package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.parent)
    LinearLayout mParent;
    @BindView(R.id.password)
    TextInputEditText mPassword;
    @BindView(R.id.confirmPassword)
    TextInputEditText mConfirmPassword;
    @BindView(R.id.changePin)
    Button mChangeBtn;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        setToolbar();
        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndChangePin();
            }
        });
    }




    private void setToolbar() {

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void checkDataAndChangePin() {
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        if (password.isEmpty() ||  password.length()<4) {
            mPassword.setError("Password Must be At least 4 characters long");
            return;
        }

        if (!confirmPassword.equals(password)) {
            mConfirmPassword.setError("Password Miss Matched");
            return;
        }
        checkNetworkAndChangePin(password, confirmPassword);

    }

    private void checkNetworkAndChangePin(String currentPin, String newPin) {
        Utils.showPB(mParent, mPb);
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.CHANGE_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.hidePB(mParent, mPb);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        Utils.makeToast("Pin has been changed");
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
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("RId", sp.getUserInfo(Constants.R_ID));
                params.put("password", newPin);

                Log.d(TAG, "MUR Change Pin Params: " + params);
                return params;
            }
        };

        mRequestQue.add(request);
    }
}
