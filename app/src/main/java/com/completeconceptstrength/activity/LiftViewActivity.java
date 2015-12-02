package com.completeconceptstrength.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.completeconceptstrength.R;

public class LiftViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_view);

        setTitle("Lift Details");

        Bundle extras = getIntent().getExtras();

        String liftName = extras.getString("name");
        String liftCategory = extras.getString("category");
        String liftType = extras.getString("type");
        String liftDescription = extras.getString("description");

        TextView nameTV = (TextView) findViewById(R.id.liftName);
        TextView catTV = (TextView) findViewById(R.id.liftCategory);
        TextView typeTV = (TextView) findViewById(R.id.liftType);
        TextView descrTV = (TextView) findViewById(R.id.liftDescription);

        nameTV.setText(liftName);
        catTV.setText(liftCategory);
        typeTV.setText(liftType);
        descrTV.setText(liftDescription);
    }

}
