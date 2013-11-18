package com.mydoc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mydoc.biz.ContentManager;
import com.mydoc.biz.PatientInfoManager;
import com.mydoc.biz.PreferenceManager;

/**
 * Created by yanga on 2013/11/02.
 */
public class MyDocActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ContentManager getContentManager(){
        return ((MyDocApplication) getApplicationContext()).getContentManager();
    }

    public PatientInfoManager getPatientInfoManager(){
        return ((MyDocApplication) getApplicationContext()).getPatientInfoManager();
    }

    public PreferenceManager getPreferenceManager(){
        return ((MyDocApplication) getApplicationContext()).getPreferenceManager();
    }

    public void showToast(Context context, String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER_VERTICAL|Gravity.TOP, 0,0);
        t.setView(layout);
        t.show();
    }
}