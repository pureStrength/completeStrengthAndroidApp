package com.completeconceptstrength.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import android.util.Log;

import completeconceptstrength.model.user.impl.Athlete;
import completeconceptstrength.model.user.impl.User;

public class AthleteSettings extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    Athlete a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_settings);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();

        try{
            a = new Athlete(user);
        }
        catch(Exception e){
            Log.e("onCreate", "Incorrect User Type");
        }

        setUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_athlete_settings, menu);
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

    public void setUserDetails(){
        EditText userFirstName = (EditText) findViewById(R.id.athleteFirstName);
        userFirstName.setText(user.getFirstName());

        EditText userLastName = (EditText) findViewById(R.id.athleteLastName);
        userLastName.setText(user.getLastName());

        EditText userOrg = (EditText) findViewById(R.id.athleteOrg);
        userOrg.setText(getOrganization());

        EditText athleteHeight = (EditText) findViewById(R.id.athleteHeight);
        athleteHeight.setText(getAthleteHeight());

        EditText athleteWeight = (EditText) findViewById(R.id.athleteWeight);
        athleteWeight.setText(getAthleteWeight());

        EditText athleteEmail = (EditText) findViewById(R.id.athleteEmail);
        athleteEmail.setText(user.getEmail());

        EditText athleteDOB = (EditText) findViewById(R.id.athleteDOB);
        athleteDOB.setText(getAthleteDOB());
    }

    public String getOrganization(){
        if(user.getOrganization() != null){
            return user.getOrganization();
        }
        else {
            return "N/A";
        }
    }

// TODO need get height function inside user
    public String getAthleteHeight() {
        if(a.getAthleteProfile().getHeight() != null){
            return a.getAthleteProfile().getHeight().toString();
        }
        return "N/A";
    }

    public String getAthleteWeight() {
        if(a.getAthleteProfile().getMostRecentWeight() != null){
            return a.getAthleteProfile().getMostRecentWeight().toString();
        }
        return "N/A";
    }

    public String getAthleteDOB() {
        if(a.getAthleteProfile().getDateOfBirth() != null){
            return a.getAthleteProfile().getDateOfBirth().toString();
        }
        else{
            return "N/A";
        }
    }

    public void editProfile(View view){
        EditText userFirstName = (EditText) findViewById(R.id.athleteFirstName);
        userFirstName.setEnabled(true);

        EditText userLastName = (EditText) findViewById(R.id.athleteLastName);
        userLastName.setEnabled(true);

        EditText userOrg = (EditText) findViewById(R.id.athleteOrg);
        userOrg.setEnabled(true);

        EditText athleteHeight = (EditText) findViewById(R.id.athleteHeight);
        athleteHeight.setEnabled(true);

        EditText athleteWeight = (EditText) findViewById(R.id.athleteWeight);
        athleteWeight.setEnabled(true);

        EditText athleteEmail = (EditText) findViewById(R.id.athleteEmail);
        athleteEmail.setEnabled(true);

        EditText dob = (EditText) findViewById(R.id.athleteDOB);
        dob.setEnabled(true);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(false);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(true);
    }

    public void saveProfile(View view){
        EditText userFirstName = (EditText) findViewById(R.id.athleteFirstName);
        if(!user.getFirstName().equals(userFirstName.getText().toString())){
            user.setFirstName(userFirstName.getText().toString());
            Log.i("saveProfile", "New First Name: " + userFirstName.getText().toString());
        }
        userFirstName.setEnabled(false);

        EditText userLastName = (EditText) findViewById(R.id.athleteLastName);
        if(!user.getLastName().equals(userLastName.getText().toString())){
            user.setLastName(userLastName.getText().toString());
            Log.i("saveProfile", "New Last Name: " + userLastName.getText().toString());
        }
        userLastName.setEnabled(false);

        EditText userOrg = (EditText) findViewById(R.id.athleteOrg);
        if(!user.getOrganization().equals(userOrg.getText().toString())){
            user.setOrganization(userOrg.getText().toString());
            Log.i("saveProfile", "New Organization: " + userOrg.getText().toString());
        }
        userOrg.setEnabled(false);

        EditText athleteHeight = (EditText) findViewById(R.id.athleteHeight);
        athleteHeight.setEnabled(false);

        EditText athleteWeight = (EditText) findViewById(R.id.athleteWeight);
        athleteWeight.setEnabled(false);

        EditText athleteEmail = (EditText) findViewById(R.id.athleteEmail);
        if(!user.getEmail().equals(athleteEmail.getText().toString())){
            user.setEmail(athleteEmail.getText().toString());
            Log.i("saveProfile", "New Email: " + athleteEmail.getText().toString());
        }
        athleteEmail.setEnabled(false);

        EditText dob = (EditText) findViewById(R.id.athleteDOB);
        dob.setEnabled(false);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(true);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

        globalContext.setLoggedInUser(user);
        //returns a boolean
        if(!globalContext.getUserClientService().update(user.getId(), user)){
            Log.e("saveProfile", "Unable to update user");
        }
    }

}
