package com.completeconceptstrength.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import completeconceptstrength.services.impl.UserClientService;
import completeconceptstrength.services.utils.IServiceClient;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserType;


public class RegistrationActivity extends ActionBarActivity {

    // Global context
    GlobalContext globalContext;

    // Properties
    private static final Boolean _REQUIRE_VERIFICATION = false;

    // Service classes
    UserClientService userService;

    // UI references.
    private EditText textFieldName;
    private EditText textFieldEmail;
    private EditText textFieldPassword;
    private EditText textFieldOrganization;
    private RadioButton radioButtonAthlete;
    private RadioButton radioButtonTrainer;
    private ProgressBar progressRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Log.d("onCreate", "Setting UI references");

        // Set UI references
        textFieldName         = (EditText) findViewById(R.id.textFieldName);
        textFieldEmail        = (EditText) findViewById(R.id.textFieldEmail);
        textFieldPassword     = (EditText) findViewById(R.id.textFieldPassword);
        textFieldOrganization = (EditText) findViewById(R.id.textFieldOrganization);
        radioButtonAthlete    = (RadioButton) findViewById(R.id.radioButtonAthlete);
        radioButtonTrainer    = (RadioButton) findViewById(R.id.radioButtonTrainer);
        progressRegister      = (ProgressBar) findViewById(R.id.progressRegister);

        // Setup register button
        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    public void attemptRegistration() {

        Log.i("attemptRegistration", "Attempting to get values from fields...");

        // Set the user type for this user
        UserType userType = null;
        if(radioButtonAthlete.isChecked()) {
            userType = UserType.ATHLETE;
        } else if(radioButtonTrainer.isChecked()) {
            userType = UserType.COACH;
        } else {
            Log.e("RegistrationActivity", "Unable to determine user type");
        }

        // Create user with fields from the UI
        final User user = new User(userType);
        user.setFirstName(textFieldName.getText().toString());
        user.setLastName(textFieldName.getText().toString());
        user.setEmail(textFieldEmail.getText().toString());
        user.setPassword(textFieldPassword.getText().toString());
        user.setOrganization(textFieldOrganization.getText().toString());

        // Execute the registration task
        final RegistrationTask registrationTask = new RegistrationTask(user);
        registrationTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private String alertTitle;
        private String alertMessage;
        private int alertIconNumber;

        RegistrationTask(final User user) {
            localUser = user;
        }

        @Override
        protected void onPreExecute() {
            // Start the progress wheel spinner
            progressRegister.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to register: " + localUser);

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
                result = userService.register(localUser, _REQUIRE_VERIFICATION);
            } else {
                Log.e("doInBackground", "userService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                alertTitle = "Unable to register";
                alertIconNumber = android.R.drawable.ic_dialog_alert;

                final HttpResponse response = userService.getLastResponse();
                if (response != null) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        Log.i("doInBackground", "User attempted to register an already existing email");
                        alertMessage = "A user already exists with that email";
                    } else {
                        Log.e("doInBackground", "Error registering user with status code: " + response.getStatusLine().getStatusCode());
                        alertMessage = "Error registering, \nPlease try again later.\n";
                    }

                } else {
                    Log.e("doInBackground", "Registration response is null");
                    alertMessage = "Unable to access server";
                }

            } else {
                alertIconNumber = android.R.drawable.ic_dialog_info;
                if(_REQUIRE_VERIFICATION) {
                    alertTitle = "Registration almost complete";
                    alertMessage = "Please check your email to complete registration";
                } else {
                    alertTitle = "Registration complete";
                    alertMessage = "You may now login to access your account";
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            // Stop the progress wheel from spinning
            progressRegister.setVisibility(View.GONE);

            // Alert the user that the request was made (the values are set in doInBackground)
            new AlertDialog.Builder(RegistrationActivity.this)
                    .setTitle(alertTitle)
                    .setMessage(alertMessage)
                    .setIcon(alertIconNumber)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        /**
                         * Once the user reads the message and clicks the button, this method is called
                         * @param dialog the dialog which the button appears
                         * @param which the id of the button clicked
                         */
                        public void onClick(DialogInterface dialog, int which) {

                            // If successful, return to the login page
                            if (success) {
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Allow the user to attempt to register again
                            }

                        }

                    })
                    .show();
        }

        @Override
        protected void onCancelled() {

        }

    }
}
