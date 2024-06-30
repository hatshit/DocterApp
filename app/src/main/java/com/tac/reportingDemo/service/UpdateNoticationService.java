package com.tac.reportingDemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.tac.reportingDemo.utils.Utils;

public class UpdateNoticationService extends BroadcastReceiver {

    public static final  String ACTION_NAME = "com.tac.doctordcr.service";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null) {
                Location location = result.getLastLocation();
                Utils.makeToast(location.getLatitude() + "");
                Log.d("TAC", "MUR background loc: " + location.getLatitude());

            }
        }
        Log.d("TAC", "MUR background loc not workimg " );

    }

}
