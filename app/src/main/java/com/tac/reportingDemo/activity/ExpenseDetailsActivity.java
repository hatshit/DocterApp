package com.tac.reportingDemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.pojo.ExpensePojo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseDetailsActivity extends AppCompatActivity {

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
    @BindView(R.id.deduction)EditText mDeduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        ButterKnife.bind(this);
        setToolbar();
        if (getIntent().hasExtra("expense")) {
            ExpensePojo pojo = new Gson().fromJson(getIntent().getStringExtra("expense"),
                    ExpensePojo.class);
            showExpenseDetails(pojo);
        } else {
            onBackPressed();
        }
    }

    private void showExpenseDetails(ExpensePojo pojo) {
        mPlaceTo.setText(pojo.getPlaceTo());
        mPlaceFrom.setText(pojo.getPlaceFrom());
        mAllowance.setText(pojo.getAllowance());
        mTotalAmount.setText(pojo.getTotalAmount());
        mRemark.setText(pojo.getRemark());
        mMode.setText(pojo.getMode());
        mMissAmount.setText(pojo.getMissAmount());
        mWorkDate.setText(pojo.getWorkDate());
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Expense Details");
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//toolbaar caolor
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }
}
