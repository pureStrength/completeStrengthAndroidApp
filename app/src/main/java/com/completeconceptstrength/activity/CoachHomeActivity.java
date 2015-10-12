package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import completeconceptstrength.model.user.impl.User;


public class CoachHomeActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_home);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        setTitle(welcomeString());
    }

    @Override
    public void onResume(){
        super.onResume();
        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        setTitle(welcomeString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coach_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openAthletesList(View view){
        Intent intent = new Intent(this, CoachAthletesActivity.class);
        startActivity(intent);
    }

    public void openWorkouts(View view){
        Intent intent = new Intent(this, CoachWorkouts.class);
        startActivity(intent);
    }

    public void openConnections(View view){
        Intent intent = new Intent(this, Connections.class);
        startActivity(intent);
    }

    public void openSettings(View view){
        Intent intent = new Intent(this, CoachSettings.class);
        startActivity(intent);
    }

    public void logout(View view){
        globalContext.setLoggedInUser(null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public String welcomeString(){
        return "Welcome " + user.getFirstName() + "!";
    }
}
