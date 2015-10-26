package com.completeconceptstrength.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import completeconceptstrength.model.user.impl.User;

public class AthleteWorkoutCalendar extends AppCompatActivity {

    GlobalContext globalContext;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workout_calendar);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
    }

}
