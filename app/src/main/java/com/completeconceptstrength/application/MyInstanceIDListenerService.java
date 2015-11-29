package com.completeconceptstrength.application;

import android.content.Intent;

import com.completeconceptstrength.activity.RegistrationActivity;
import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;
/**
 * Created by Jessica on 11/28/2015.
 */

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated.
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}

