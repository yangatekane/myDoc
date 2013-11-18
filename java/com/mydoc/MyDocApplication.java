package com.mydoc;

import android.app.Application;

import com.mydoc.biz.ContentManager;
import com.mydoc.biz.PatientInfoManager;
import com.mydoc.biz.PreferenceManager;

/**
 * Created by yanga on 2013/11/02.
 */
public class MyDocApplication extends Application {
    private ContentManager contentManager;
    private PatientInfoManager patientInfoManager;
    private PreferenceManager preferenceManager;

    public MyDocApplication() {
        contentManager = new ContentManager(this);
        patientInfoManager = new PatientInfoManager(this);
        preferenceManager = new PreferenceManager(this);
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

    public PatientInfoManager getPatientInfoManager(){
        return patientInfoManager;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}
