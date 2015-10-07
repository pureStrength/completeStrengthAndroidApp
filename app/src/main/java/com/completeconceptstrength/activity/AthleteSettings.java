package com.completeconceptstrength.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.exercise.impl.PreferenceUnitType;
import completeconceptstrength.model.user.impl.Athlete;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserType;
import completeconceptstrength.services.impl.UserClientService;

public class AthleteSettings extends AppCompatActivity {

    GlobalContext globalContext;
    UserClientService userService;
    User user;
    Athlete a;
    private UserVerifyTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_settings);

        globalContext = (GlobalContext)getApplicationContext();
        userService = globalContext.getUserClientService();
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
        ImageView userProfilePic = (ImageView) findViewById(R.id.athleteProfPic);

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

        RadioGroup unitsOfMeasurement = (RadioGroup) findViewById(R.id.athleteRadioGroup);
        int selectedID = unitsOfMeasurement.getCheckedRadioButtonId();
        if(selectedID == R.id.radioButtonImperial){
            user.setPreferenceUnitType(PreferenceUnitType.IMPERIAL);
        }
        else {
            user.setPreferenceUnitType(PreferenceUnitType.METRIC);
        }

        EditText dob = (EditText) findViewById(R.id.athleteDOB);
        dob.setEnabled(false);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(true);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

        globalContext.setLoggedInUser(user);

        final UpdateProfileTask updateTask = new UpdateProfileTask(user);
        updateTask.execute((Void) null);
    }

    public void changePassword(View view){
        final AlertDialog.Builder changePass = new AlertDialog.Builder(this);

        final EditText currPassword = new EditText(this);
        currPassword.setHint("Current Password");
        final EditText newPassword = new EditText(this);
        newPassword.setHint("New Password");
        final EditText verifyNewPassword = new EditText(this);
        verifyNewPassword.setHint("Verify New Password");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(currPassword);
        linearLayout.addView(newPassword);
        linearLayout.addView(verifyNewPassword);

        changePass.setView(linearLayout);

        changePass.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currP = currPassword.getText().toString().trim();

                String newP = newPassword.getText().toString().trim();
                String vNewP = verifyNewPassword.getText().toString().trim();

                mAuthTask = new UserVerifyTask(user.getEmail(), currP);
                mAuthTask.execute((Void) null);

                if (!newP.equals(vNewP)) {
                    // can't update because new passwords aren't the same
                } else if (mAuthTask == null) {
                    Log.e("changePassword", "Could not authenticate");
                } else {
                    user.setPassword(newP);
                    globalContext.setLoggedInUser(user);

                    final UpdateProfileTask updateTask = new UpdateProfileTask(user);
                    updateTask.execute((Void) null);
                }
            }
        });

        changePass.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        changePass.setTitle("Change Password")
                .setCancelable(true)
                .show();
    }

    /**
     * Represents an asynchronous profile update task used to update
     * the user's details.
     */
    public class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private String alertTitle;
        private String alertMessage;
        private int alertIconNumber;

        UpdateProfileTask(final User user) {
            localUser = user;
        }

        @Override
        protected void onPreExecute() {
            // Start the progress wheel spinner
            //progressRegister.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to update: " + localUser);

            // Set service class
            if(userService == null) {

                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }

                userService = globalContext.getUserClientService();
            }


            // Run the service
            if(userService != null) {
                result = userService.update(localUser.getId(), localUser);
            } else {
                Log.e("doInBackground", "userService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                alertTitle = "Unable to update settings";
                alertIconNumber = android.R.drawable.ic_dialog_alert;

                final HttpResponse response = userService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error updating user with status code: " + response.getStatusLine().getStatusCode());
                    alertMessage = "Error updating, \nPlease try again later.\n";
                }
                else {
                    Log.e("doInBackground", "Update user response is null");
                    alertMessage = "Unable to access server";
                }

            } else {
                alertIconNumber = android.R.drawable.ic_dialog_info;
                alertTitle = "Changes saved";
                alertMessage = "Settings have been updated";
            }

            return result;
        }
    }

    private class UserVerifyTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        User user;

        UserVerifyTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            user = null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            // Set service class
            if(userService == null) {

                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }

                userService = globalContext.getUserClientService();
            }

            // Run the service
            if(userService != null) {
                user = userService.authenticate(mEmail, mPassword);
            } else {
                Log.e("doInBackground", "userService is null");
            }

            Log.d("doInBackground", "result: " + result);

            if(user != null){
                result = true;
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                globalContext.setLoggedInUser(user);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

}
