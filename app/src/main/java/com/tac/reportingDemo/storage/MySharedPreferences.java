package com.tac.reportingDemo.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    public static SharedPreferences mSharedPreferences;
    public static MySharedPreferences mInstance;
    public static Context mContext;
    private String sp_name = "PUB_MEMBER";
    private String DEFAULT = "";

    public MySharedPreferences() {
        mSharedPreferences = mContext.getSharedPreferences(sp_name, Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    public void setLoginSteps(String step) {
        mSharedPreferences.edit().putString("step", step).apply();
    }

    public String getLoginSteps() {
        return mSharedPreferences.getString("step", "null");
    }

    public void setBillingType(String type) {
        mSharedPreferences.edit().putString("type", type).apply();
    }

    public String getBillingType() {
        return mSharedPreferences.getString("type", "");
    }

    public void addProduct(String value) {
        mSharedPreferences.edit().putString("product", value).apply();

    }

    public String getProduct() {
        return mSharedPreferences.getString("product", DEFAULT);
    }

    public void addProductId(String value) {
        mSharedPreferences.edit().putString("productId", value).apply();

    }

    public String getProductId() {
        return mSharedPreferences.getString("productId", DEFAULT);
    }

    public void addGiftIds(String value) {
        mSharedPreferences.edit().putString("giftId", value).apply();

    }

    public String getGiftIds() {
        return mSharedPreferences.getString("giftId", DEFAULT);
    }

    public void addGifts(String value) {
        mSharedPreferences.edit().putString("gift", value).apply();

    }

    public String getGifts() {
        return mSharedPreferences.getString("gift", DEFAULT);
    }

    public void addSampleIds(String value) {
        mSharedPreferences.edit().putString("sampleId", value).apply();

    }

    public String getSampleIds() {
        return mSharedPreferences.getString("sampleId", DEFAULT);
    }


    public void addSamples(String value) {
        mSharedPreferences.edit().putString("sample", value).apply();

    }

    public String getSamples() {
        return mSharedPreferences.getString("sample", DEFAULT);
    }

    public void setUserInfo(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();

    }

    public void setHQID(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();

    }

    public boolean isSubscribed(){
        return mSharedPreferences.getBoolean("isSubscribed",false);
    }
    public void subscribedToNoti(){
        mSharedPreferences.edit().putBoolean("isSubscribed",true);
    }
    public String getUserInfo(String key) {
        return mSharedPreferences.getString(key, DEFAULT);
    }

    public String getHq_id(String key) {
        return mSharedPreferences.getString(key, DEFAULT);
    }

    public void setReportInfo(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();

    }

    public String getReportInfo(String key) {
        return mSharedPreferences.getString(key, DEFAULT);
    }


    public void clearData() {
        mSharedPreferences.edit().clear().apply();
    }
}
