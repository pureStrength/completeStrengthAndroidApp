package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.completeconceptstrength.R;

public class CoachWorkouts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_workouts);

        setTitle("Workouts");
    }

    public void openLiftsList(View view){
        Intent intent = new Intent(this, CoachWorkoutsLifts.class);
        startActivity(intent);
    }

    public void openSetsList(View view){
        Intent intent = new Intent(this, CoachWorkoutsSets.class);
        startActivity(intent);
    }

    public void openPrescription(View view){
        Intent intent = new Intent(this, CoachWorkoutsPrescriptions.class);
        startActivity(intent);
    }

}
