package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.config.LocalServer;

import javax.ws.rs.core.MediaType;

import completeconceptstrength.serialization.CustomJsonSerializer;
import completeconceptstrength.services.impl.UserClientService;
import completeconceptstrength.services.utils.IServiceClientWrapper;
import completeconceptstrength.services.utils.ServiceClient;
import completeconceptstrength.user.model.impl.Athlete;
import completeconceptstrength.user.model.impl.User;
import completeconceptstrength.user.model.impl.UserType;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Log.d("onCreate", "Creating services");

        // Get the global context
        globalContext = (GlobalContext)getApplicationContext();

        // Set service classes
        // Create the service client
        IServiceClientWrapper serviceClient = globalContext.getServiceClient();

        // Create the athlete service
        userService = new UserClientService(serviceClient, LocalServer.IP_ADDRESS,
                LocalServer.IP_PORT);

        Log.d("onCreate", "ServiceClient: " + serviceClient.toString());

        Log.d("onCreate", "Setting UI references");
        // Set UI references
        textFieldName = (EditText) findViewById(R.id.textFieldName);
        textFieldEmail = (EditText) findViewById(R.id.textFieldEmail);
        textFieldPassword = (EditText) findViewById(R.id.textFieldPassword);
        textFieldOrganization = (EditText) findViewById(R.id.textFieldOrganization);
        radioButtonAthlete = (RadioButton) findViewById(R.id.radioButtonAthlete);
        radioButtonTrainer = (RadioButton) findViewById(R.id.radioButtonTrainer);

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

        RegistrationTask(final User user) {
            localUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to register: " + localUser);

            // Run the service
            if(userService != null) {
                result = userService.register(localUser, _REQUIRE_VERIFICATION);
            } else {
                Log.e("doInBackground", "userService is null");
            }

            Log.d("doInBackground", "result: " + result);

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {

        }

    }
}
