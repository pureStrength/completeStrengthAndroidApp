package com.completeconceptstrength.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftSetClientService;

public class CoachWorkoutsSets extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftSetClientService setService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_workouts_sets);

        setTitle("Sets");

        globalContext = (GlobalContext) getApplicationContext();
        user = globalContext.getLoggedInUser();
        setService = globalContext.getMainLiftSetClientService();

    }

}
