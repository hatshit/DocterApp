package com.tac.reportingDemo.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.tac.reportingDemo.R;
import com.tac.reportingDemo.infratructure.MyApplication;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class Utils {

    private static final String TAG = "UTILS";

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected();
    }

    public static void showRedSnackbar(View layoutForSnacbar, String message) {
        Snackbar snack = Snackbar.make(layoutForSnacbar, message, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snack.getView();
        group.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorAccent));
        snack.show();
    }


    public static String getTime() {
        long ts = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        return formatter.format(new Date(ts));
    }

    public static String getDate(String time) {

        long ts = Long.parseLong(time);
        Date date = new Date(ts); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy "); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        return sdf.format(date);
    }

    public static String getDay(String timestamp) {

        long ts = (Long.parseLong(timestamp)) * 1000;
        Date date = new Date(ts); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        return sdf.format(date);
    }

    public static String getMonth(String timestamp) {

        long ts = (Long.parseLong(timestamp)) * 1000;
        Date date = new Date(ts); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("MMM"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        return sdf.format(date);
    }

    public static Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("token", "Fd454fF45tadfl464ak4wkjprj");
        return header;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void makeToast(String message) {
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void noInternetToast() {
        Toast.makeText(MyApplication.getAppContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

    public static void parsingErrorToast() {
        Toast.makeText(MyApplication.getAppContext(), "oops! something went wrong!", Toast.LENGTH_SHORT).show();
    }

    public static void showPB(View parent, View pb) {
        parent.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }

    public static void hidePB(View parent, View pb) {
        parent.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month.substring(0,3);
    }

}
