package com.completeconceptstrength.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import completeconceptstrength.model.user.impl.User;


public class AthleteHomeActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_home);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        setTitle(welcomeString());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_athlete_home, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        setTitle(welcomeString());
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

    public void openSettings(View view){
        Intent intent = new Intent(this, AthleteSettings.class);
        startActivity(intent);
    }

    public void openWorkouts(View view){
        Intent intent = new Intent(this, AthleteWorkouts.class);
        startActivity(intent);
    }

    public void openConnections(View view){
        Intent intent = new Intent(this, ConnectionsButtons.class);
        startActivity(intent);
    }

    public void logout(View view){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("username");

        globalContext.setLoggedInUser(null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public String welcomeString(){
        return "Welcome " + user.getFirstName() + "!";
    }
}
