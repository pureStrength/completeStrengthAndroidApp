package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.completeconceptstrength.R;

public class AthleteWorkouts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workouts);

        setTitle("Prescriptions");
    }

    public void openCalendarView(View view){
        Intent intent = new Intent(this, AthleteWorkoutCalendar.class);
        startActivity(intent);
    }

    public void openListView(View view){
        Intent intent = new Intent(this, AthleteWorkoutList.class);
        startActivity(intent);
    }

}
