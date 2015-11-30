package com.completeconceptstrength.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.RegistrationIntentService;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import completeconceptstrength.model.exercise.impl.OneRepMaxChart;
import completeconceptstrength.model.user.impl.Athlete;
import completeconceptstrength.model.user.impl.CellCarrier;
import completeconceptstrength.model.user.impl.User;
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

        TableLayout ORMtable = (TableLayout) findViewById(R.id.ORMTable);
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

    public List<OneRepMaxChart> getAthleteORMs() {
        return a.getAthleteProfile().getMostRecentOneRepMaxes();
    }

    public void enableAndroid(View view){
        Intent regIntent = new Intent(AthleteSettings.this, RegistrationIntentService.class);
        startService(regIntent);
    }

    public void enableText(View view) {
        final AlertDialog.Builder enableTexts = new AlertDialog.Builder(this);

        final EditText phoneNumber = new EditText(this);
        phoneNumber.setHint("Phone Number");
        final Spinner carriers = new Spinner(this); // ENUM, iterate, class in api cellCarrier.values

        ArrayList<String> spinnerList = new ArrayList<String>();

        for(CellCarrier c : CellCarrier.values()){
            spinnerList.add(c.getType());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
        carriers.setAdapter(adapter);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(phoneNumber);
        linearLayout.addView(carriers);
        linearLayout.setPadding(10, 10, 10, 10);

        enableTexts.setView(linearLayout);

        enableTexts.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("posBtn", phoneNumber.getText().toString());
                long phoneNum = Long.valueOf(phoneNumber.getText().toString());
                String carrier = carriers.getSelectedItem().toString();

                user.setPhoneNumber(phoneNum);
                user.setCellCarrier(CellCarrier.fromString(carrier));
                user.setEnableTextMessages(true);

                final UpdateProfileTask updateTask = new UpdateProfileTask(user);
                updateTask.execute((Void) null);
            }
        });

        enableTexts.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        enableTexts.setTitle("Enable Text Notifications")
                .setCancelable(true)
                .show();
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
    }
}
