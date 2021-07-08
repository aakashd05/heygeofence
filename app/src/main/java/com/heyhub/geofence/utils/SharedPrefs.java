package com.heyhub.geofence.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heyhub.geofence.models.FenceModel;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefs {

    static String TAG = "FencePrefs";
    static String FENCE_LIST = "FENCE_LIST";

    public static ArrayList<FenceModel> fetchFenceList(Context context) {
        ArrayList<FenceModel> returnString = new ArrayList<>();
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
            String json = sp.getString(FENCE_LIST, "");
            Type type = new TypeToken<ArrayList<FenceModel>>() {
            }.getType();
            if (!json.isEmpty())
                returnString = new Gson().fromJson(json, type);
        }
        return returnString;
    }

    public static void storeFenceList(Context context, ArrayList<FenceModel> value) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String json = new Gson().toJson(value);
            editor.putString(FENCE_LIST, json);
            editor.apply();
        }
    }
}
