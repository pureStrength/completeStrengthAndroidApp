package com.completeconceptstrength.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import completeconceptstrength.model.user.impl.User;

public class ViewProfileActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();

        Bundle extra = getIntent().getExtras();
        long profileViewID = extra.getLong("profileID");

        // TODO can I combine these 2 things?
        // connection status task

        // user profile task
    }

    // TODO Async task to see if connection exists

    // TODO Async task to get user profile
}
