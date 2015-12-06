package com.completeconceptstrength.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.RegistrationIntentService;

import org.apache.http.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import completeconceptstrength.model.exercise.impl.OneRepMaxChart;
import completeconceptstrength.model.exercise.impl.PreferenceUnitType;
import completeconceptstrength.model.exercise.impl.TrackEventChart;
import completeconceptstrength.model.exercise.impl.TrackTime;
import completeconceptstrength.model.user.impl.AthleteProfile;
import completeconceptstrength.model.user.impl.CellCarrier;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.AthleteClientService;
import completeconceptstrength.services.impl.UserClientService;

public class AthleteSettings extends AppCompatActivity {

    GlobalContext globalContext;
    UserClientService userService;
    AthleteClientService athleteClientService;
    User user;
    AthleteProfile athleteProfile;
    boolean useKGUnits;

    private UserVerifyTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_settings);

        globalContext = (GlobalContext)getApplicationContext();
        userService = globalContext.getUserClientService();
        athleteClientService = globalContext.getAthleteClientService();
        user = globalContext.getLoggedInUser();

        useKGUnits = user.getPreferenceUnitType().equals(PreferenceUnitType.METRIC);

        final GetAthleteProfileInfo getProfileTask = new GetAthleteProfileInfo(user.getId());
        getProfileTask.execute((Void) null);

        RadioButton metricRadioButton = (RadioButton) findViewById(R.id.metric);
        RadioButton imperialRadioButton = (RadioButton) findViewById(R.id.imperial);

        metricRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    user.setPreferenceUnitType(PreferenceUnitType.METRIC);

                    final UpdateProfileTask updateTask = new UpdateProfileTask(user);
                    updateTask.execute((Void) null);
                }
            }
        });

        imperialRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    user.setPreferenceUnitType(PreferenceUnitType.IMPERIAL);

                    final UpdateProfileTask updateTask = new UpdateProfileTask(user);
                    updateTask.execute((Void) null);
                }
            }
        });

        if(useKGUnits){
            metricRadioButton.setChecked(true);
        }
        else{
            imperialRadioButton.setChecked(true);
        }
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

        Button enableAndroid = (Button) findViewById(R.id.enableAndroidButton);
        if(user.getEnableAndroidNotifications()){
            enableAndroid.setBackgroundColor(Color.parseColor("#ff3333"));
            enableAndroid.setText("Disable Android Notifications");
        }
        else{
            enableAndroid.setBackgroundColor(Color.parseColor("#3384ff"));
            enableAndroid.setText("Enable Android Notifications");
        }

        Button enableText = (Button) findViewById(R.id.enableTexts);
        if(user.getEnableTextMessages()){
            enableText.setBackgroundColor(Color.parseColor("#ff3333"));
            enableText.setText("Disable Text Notifications");
        }
        else{
            enableText.setBackgroundColor(Color.parseColor("#3384ff"));
            enableText.setText("Enable Text Notifications");
        }
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
        if(athleteProfile.getHeight() != null){
            return athleteProfile.getHeight().toString();
        }
        return "N/A";
    }

    public String getAthleteWeight() {
        if(athleteProfile.getMostRecentWeight() != null){
            return athleteProfile.getMostRecentWeight().toString();
        }
        return "N/A";
    }

    public String getAthleteDOB() {
        if(athleteProfile.getDateOfBirth() != null){
            return athleteProfile.getDateOfBirth().toString();
        }
        else{
            return "N/A";
        }
    }

    public void enableAndroid(View view){
        if(!user.getEnableAndroidNotifications()){
            Intent regIntent = new Intent(AthleteSettings.this, RegistrationIntentService.class);
            startService(regIntent);
        }
        else{
            user.setEnableAndroidNotifications(false);

            // Updates the user in the application context and server side
            globalContext.setLoggedInUser(user);

            final UpdateProfileTask updateTask = new UpdateProfileTask(user);
            updateTask.execute((Void) null);
        }

        recreate();
    }

    public void enableText(View view) {

        if(user.getEnableTextMessages()){
            user.setEnableTextMessages(false);

            // Updates the user in the application context and server side
            globalContext.setLoggedInUser(user);

            final UpdateProfileTask updateTask = new UpdateProfileTask(user);
            updateTask.execute((Void) null);

            recreate();

            return;
        }

        final AlertDialog.Builder enableTexts = new AlertDialog.Builder(this);

        final EditText phoneNumber = new EditText(this);
        phoneNumber.setHint("Phone Number");
        final Spinner carriers = new Spinner(this);

        ArrayList<String> spinnerList = new ArrayList<>();

        for(CellCarrier c : CellCarrier.values()){
            spinnerList.add(c.getType());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
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

                recreate();
            }
        });

        enableTexts.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                recreate();
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

    /**
     * Checks for changes to the user's profile upon clicking the Save button
     * @param view current view
     */
    public void saveProfile(View view){
        // Checks if the first name needs to be updated
        EditText userFirstName = (EditText) findViewById(R.id.athleteFirstName);
        if(!user.getFirstName().equals(userFirstName.getText().toString())){
            user.setFirstName(userFirstName.getText().toString());
            Log.i("saveProfile", "New First Name: " + userFirstName.getText().toString());
        }
        userFirstName.setEnabled(false);

        // Checks if the last name needs to be updated
        EditText userLastName = (EditText) findViewById(R.id.athleteLastName);
        if(!user.getLastName().equals(userLastName.getText().toString())){
            user.setLastName(userLastName.getText().toString());
            Log.i("saveProfile", "New Last Name: " + userLastName.getText().toString());
        }
        userLastName.setEnabled(false);

        // Checks if the organization needs to be updated
        EditText userOrg = (EditText) findViewById(R.id.athleteOrg);
        if(!user.getOrganization().equals(userOrg.getText().toString())){
            user.setOrganization(userOrg.getText().toString());
            Log.i("saveProfile", "New Organization: " + userOrg.getText().toString());
        }
        userOrg.setEnabled(false);

        // Checks if the athlete height needs to be updated
        EditText athleteHeight = (EditText) findViewById(R.id.athleteHeight);
        athleteHeight.setEnabled(false);

        // Checks if the athlete weight needs to be updated
        EditText athleteWeight = (EditText) findViewById(R.id.athleteWeight);
        athleteWeight.setEnabled(false);

        // Checks if the athlete email needs to be updated
        EditText athleteEmail = (EditText) findViewById(R.id.athleteEmail);
        if(!user.getEmail().equals(athleteEmail.getText().toString())){
            user.setEmail(athleteEmail.getText().toString());
            Log.i("saveProfile", "New Email: " + athleteEmail.getText().toString());
        }
        athleteEmail.setEnabled(false);

        // Checks if the athlete date of birth needs to be updated
        EditText dob = (EditText) findViewById(R.id.athleteDOB);
        dob.setEnabled(false);

        // Re-enables edit button
        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(true);

        // Disables save button
        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

        // Updates the user in the application context and server side
        globalContext.setLoggedInUser(user);

        final UpdateProfileTask updateTask = new UpdateProfileTask(user);
        updateTask.execute((Void) null);
    }

    /**
     * Allows the athlete user to change their password through the settings page
     * @param view current view
     */
    public void changePassword(View view){
        //Builds the window that pops up upon clicking change password
        final AlertDialog.Builder changePass = new AlertDialog.Builder(this);

        // User must input their current password, new password, and verify the new password
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

        // On clicking OK, the new password gets pushed to the server if all is well
        changePass.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currP = currPassword.getText().toString().trim();

                String newP = newPassword.getText().toString().trim();
                String vNewP = verifyNewPassword.getText().toString().trim();

                // verifies that the user email and current password are valid
                mAuthTask = new UserVerifyTask(user.getEmail(), currP);
                mAuthTask.execute((Void) null);

                // Verifies the new password matches the verification password
                // and that the old pass word is valid
                if (!newP.equals(vNewP)) {
                    // can't update because new passwords aren't the same
                } else if (mAuthTask == null) {
                    // User email and current password did not match
                    Log.e("changePassword", "Could not authenticate");
                } else {
                    //Everything has been input correctly and the users profile is updated
                    user.setPassword(newP);
                    globalContext.setLoggedInUser(user);

                    final UpdateProfileTask updateTask = new UpdateProfileTask(user);
                    updateTask.execute((Void) null);
                }
            }
        });

        // Closes change password window without performing any actions
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

            // Run the service, update the user's profile with the changed information
            if(userService != null) {
                result = userService.update(localUser.getId(), localUser);
            } else {
                Log.e("doInBackground", "userService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(!result) {
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

    /**
     * Supplies the athlete's settings page with profile information including
     *  the most recent One Rep Max values, and best track event performances
     */
    public void setProfile(){
        setORMTable();
        setTrackEventTable();
    }

    /**
     * Gets the athlete's most recent one rep maxes and adds them to the
     * One Rep Max table with lift name, weight lifted, and date lifted
     */
    public void setORMTable(){
        TableLayout ORMTable = (TableLayout) findViewById(R.id.ORMTable);

        List<OneRepMaxChart> ORMs = athleteProfile.getMostRecentOneRepMaxes();
        Log.i("Length ORMS: ", Integer.toString(athleteProfile.getMostRecentOneRepMaxes().size()));

        // Iterate through each one rep max chart belonging to this athlete
        // and create a new row to add to the ORM table
        for(OneRepMaxChart O : ORMs){
            TableRow tr = new TableRow(this);

            TextView liftName = new TextView(this);
            TextView value = new TextView(this);
            TextView dateUpdated = new TextView(this);

            // Name of lift for this one rep max
            liftName.setText(O.getLiftName());
            liftName.setTextSize(18);
            liftName.setPadding(0, 0, 40, 0);

            // Weight lifted for this one rep max
            String ORMvalue = Integer.toString(O.getMostRecentOneRepMax().getValue());

            // If there is a value for the weight lifted, add it to the current table row
            // along with the date lifted
            if(ORMvalue != null) {

                int ormWeight = useKGUnits ? convertToKG(O.getMostRecentOneRepMax().getValue()) : O.getMostRecentOneRepMax().getValue();

                String units = useKGUnits ? "kg" : "lbs";

                value.setText(ormWeight + units);
                value.setTextSize(18);
                value.setPadding(0, 0, 40, 0);

                // Date this one rep max was updated
                Date updateDate = O.getMostRecentOneRepMax().getDate();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yy");

                dateUpdated.setText("on " + sdf.format(updateDate));
                dateUpdated.setTextSize(18);
            }

            tr.addView(liftName);
            tr.addView(value);
            tr.addView(dateUpdated);

            TableLayout.LayoutParams tableRowParams= new TableLayout.LayoutParams
                    (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=10;
            int rightMargin=10;
            int bottomMargin = 2;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            tr.setLayoutParams(tableRowParams);

            ORMTable.addView(tr);
        }
    }

    /**
     * Gets the athlete's best track event times and adds them to the
     * time table on with event name, time it took, and date it occurred
     */
    public void setTrackEventTable(){
        TableLayout timeTable = (TableLayout) findViewById(R.id.timeTable);

        List<TrackEventChart> times = athleteProfile.getBestTrackTimes();

        // Iterate through each track event chart belonging to this athlete
        // and create a new row to add to the time table
        for(TrackEventChart t : times){
            TableRow tr = new TableRow(this);

            TextView eventName = new TextView(this);
            TextView value = new TextView(this);
            TextView dateUpdated = new TextView(this);

            // Gets the name for this event
            eventName.setText(t.getEventName());
            eventName.setTextSize(18);
            eventName.setPadding(0, 0, 20, 0);

            // Gets the time for this event
            TrackTime eventValue = t.getBestTrackTime().getTrackTime();
            String trackTime = eventValue.getHours() + "h " + eventValue.getMinutes() + "m " + eventValue.getSeconds() + "s";

            value.setText(trackTime);
            value.setTextSize(18);
            value.setPadding(0, 0, 20, 0);

            //Gets the date for this event
            Date updateDate = t.getBestTrackTime().getDate();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yy");

            dateUpdated.setText("on " + sdf.format(updateDate));
            dateUpdated.setTextSize(18);

            tr.addView(eventName);
            tr.addView(value);
            tr.addView(dateUpdated);

            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=10;
            int rightMargin=10;
            int bottomMargin = 2;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            tr.setLayoutParams(tableRowParams);

            timeTable.addView(tr);
        }
    }

    public class GetAthleteProfileInfo extends AsyncTask<Void, Void, Boolean>{

        private long userID;

        GetAthleteProfileInfo(final long userID){
            this.userID = userID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User ID to get connections: " + userID);

            // Set service class
            if(athleteClientService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                athleteClientService = globalContext.getAthleteClientService();
            }

            // Run the service
            if(athleteClientService != null) {
                athleteProfile = athleteClientService.getAthleteProfile(userID);

                if(athleteProfile!= null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "user client service is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = athleteClientService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user profile with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user profile response is null");
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            setUserDetails();
            setProfile();
        }
    }

    public int convertToKG(double weightInKG){
        return (int) Math.ceil(weightInKG/2.2);
    }

}
