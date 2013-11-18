package com.mydoc.biz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

/**
 * Created by yanga on 2013/11/13.
 */
public class PreferenceManager {
    private static final String TAG = PatientInfoManager.class.getName();
    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getUserPreferences() throws ClassNotFoundException, IOException {
        return context.getSharedPreferences(String.format("%s:settings", "myDoc"), Context.MODE_PRIVATE);
    }

    public void saveOperationType(String type) {
        try {
            SharedPreferences sharedPref = getUserPreferences();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("type",type);
            editor.commit();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public String getOperationType(){
        try {
            SharedPreferences sharedPref = getUserPreferences();
            return sharedPref.getString("type","");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG,e.getMessage(),e);
        }
        return "";
    }
}
