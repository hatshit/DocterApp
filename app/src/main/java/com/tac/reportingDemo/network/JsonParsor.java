package com.tac.reportingDemo.network;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParsor {
    public static String simpleParser(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        return obj.getString("message");
    }
    public static boolean isReqSuccesful(String response) throws JSONException {
        JSONObject object = new JSONObject(response);
        return object.getBoolean("result");
    }

}

