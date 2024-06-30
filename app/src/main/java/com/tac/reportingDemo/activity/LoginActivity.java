package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.network.JsonParsor;
import com.tac.reportingDemo.network.MyVolley;
import com.tac.reportingDemo.storage.Constants;
import com.tac.reportingDemo.storage.ENDPOINTS;
import com.tac.reportingDemo.storage.MySharedPreferences;
import com.tac.reportingDemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TAC";
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.login)
    Button mLogin;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.parent)
    RelativeLayout mParent;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.privacy)
    TextView mPrivacy;
    RequestQueue mRequestQue;
    MySharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = MySharedPreferences.getInstance(this);

        if (sp.getLoginSteps().equals("1")) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_main);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        ButterKnife.bind(this);

        mRequestQue = MyVolley.getInstance().getRequestQueue();


        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataAndLogin();
            }
        });

        mPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://torgemreporting.flycricket.io/privacy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
//
//        mUsername.setText("admin");
//        mPassword.setText("admin@123");
//loginBypass();
    }

    private void checkDataAndLogin() {
        String username, password;
        username = mUsername.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Utils.makeToast("Please Enter a Valid Username");
            return;
        }
        if (password.isEmpty() || password.length() < 4) {
            Utils.makeToast("Please Enter a Valid Password");
        } else {
            checkInternetAndLogin(username, password);
        }
    }

    private void checkInternetAndLogin(String username, String password) {
      Log.e("126",username+" "+password);
        if (Utils.isNetworkAvailable()) {
            login(username, password);
        } else {
            Utils.noInternetToast();
        }
    }

    private void login(final String username, final String password) {
        Utils.showPB(mParent, mPb);
//        Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, ENDPOINTS.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.hidePB(mParent, mPb);
//                Log.d(TAG, "MUR LOGIN CREDENTIALS: " + username + " / " + password);

//                Log.d(TAG, "MUR LOGIN onResponse: " + response);
                try {
                    if (JsonParsor.isReqSuccesful(response)) {
                        Utils.makeToast("Logged In");
                        JSONObject object = new JSONObject(response);
                        JSONArray dataArray = object.getJSONArray("data");
                        JSONObject data = dataArray.getJSONObject(0);
                        String rid = data.getString("RId");
                        String hq_id = data.getString("HQid");
                        FirebaseMessaging.getInstance().subscribeToTopic("notice");
                        sp.setUserInfo(Constants.USERNAME, username);
                        sp.setLoginSteps("1");
                        sp.setUserInfo(Constants.NAME, data.getString("Name"));
                        sp.setUserInfo(Constants.R_ID, rid);
                        sp.setHQID(Constants.HQ_id, hq_id);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        Utils.makeToast(JsonParsor.simpleParser(response));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.makeToast("Oops! Something Went Wrong.");

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "MUR LOGIN onError: "+ error.toString());
                Utils.hidePB(mParent, mPb);
                Utils.makeToast(getString(R.string.something_went_wrong));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/x-www-form-urlencoded");
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("userid", username);
                params.put("userpwd", password);
                Log.d(TAG, "MUR LOGIN PARAMS: " + params);

                return params;
            }
        };
        mRequestQue.add(request);
    }
public void loginBypass(){
    Utils.makeToast("Logged In");
    String rid = "121";
    FirebaseMessaging.getInstance().subscribeToTopic("notice");
    sp.setUserInfo(Constants.USERNAME, "dev");
    sp.setLoginSteps("1");
    sp.setUserInfo(Constants.NAME, "Name");
    sp.setUserInfo(Constants.R_ID, rid);
    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);

}
}
