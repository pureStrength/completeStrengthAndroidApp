package com.completeconceptstrength.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.RegistrationIntentService;

import org.apache.http.HttpResponse;

import java.util.ArrayList;

import completeconceptstrength.model.user.impl.CellCarrier;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.UserClientService;

public class CoachSettings extends ActionBarActivity {

    GlobalContext globalContext;
    User user;
    UserClientService userService;
    private UserVerifyTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_settings);
        setTitle("Settings");

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        userService = globalContext.getUserClientService();

        setUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coach_settings, menu);
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
        EditText userFirstName = (EditText) findViewById(R.id.coachFirstName);
        userFirstName.setText(user.getFirstName());

        EditText userLastName = (EditText) findViewById(R.id.coachLastName);
        userLastName.setText(user.getLastName());

        EditText userOrg = (EditText) findViewById(R.id.coachOrg);
        userOrg.setText(getOrganization());

        EditText coachEmail = (EditText) findViewById(R.id.coachEmail);
        coachEmail.setText(user.getEmail());

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

    public void editProfile(View view){
        EditText userFirstName = (EditText) findViewById(R.id.coachFirstName);
        userFirstName.setEnabled(true);

        EditText userLastName = (EditText) findViewById(R.id.coachLastName);
        userLastName.setEnabled(true);

        EditText userOrg = (EditText) findViewById(R.id.coachOrg);
        userOrg.setEnabled(true);

        EditText coachEmail = (EditText) findViewById(R.id.coachEmail);
        coachEmail.setEnabled(true);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(false);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(true);
    }

    public void saveProfile(View view){
        EditText userFirstName = (EditText) findViewById(R.id.coachFirstName);
        if(!user.getFirstName().equals(userFirstName.getText().toString())){
            user.setFirstName(userFirstName.getText().toString());
            Log.i("saveProfile", "New First Name: " + userFirstName.getText().toString());
        }
        userFirstName.setEnabled(false);

        EditText userLastName = (EditText) findViewById(R.id.coachLastName);
        if(!user.getLastName().equals(userLastName.getText().toString())){
            user.setLastName(userLastName.getText().toString());
            Log.i("saveProfile", "New Last Name: " + userLastName.getText().toString());
        }
        userLastName.setEnabled(false);

        EditText userOrg = (EditText) findViewById(R.id.coachOrg);
        if(!user.getOrganization().equals(userOrg.getText().toString())){
            user.setOrganization(userOrg.getText().toString());
            Log.i("saveProfile", "New Organization: " + userOrg.getText().toString());
        }
        userOrg.setEnabled(false);

        EditText coachEmail = (EditText) findViewById(R.id.coachEmail);
        if(!user.getEmail().equals(coachEmail.getText().toString())){
            user.setEmail(coachEmail.getText().toString());
            Log.i("saveProfile", "New Email: " + coachEmail.getText().toString());
        }
        coachEmail.setEnabled(false);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(true);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

        globalContext.setLoggedInUser(user);

        final UpdateProfileTask updateTask = new UpdateProfileTask(user);
        updateTask.execute((Void) null);
    }

    public void enableAndroid(View view){
        if(!user.getEnableAndroidNotifications()){
            Intent regIntent = new Intent(CoachSettings.this, RegistrationIntentService.class);
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


    public void changeCoachPassword(View view){
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
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
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
