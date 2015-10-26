package com.completeconceptstrength.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;

import completeconceptstrength.model.user.impl.User;

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
