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
import com.completeconceptstrength.config.LocalServer;

import javax.ws.rs.core.MediaType;

import completeconceptstrength.serialization.CustomJsonSerializer;
import completeconceptstrength.services.impl.AthleteClientService;
import completeconceptstrength.services.utils.ServiceClient;
import completeconceptstrength.user.model.impl.Athlete;


public class RegistrationActivity extends ActionBarActivity {

    // Service classes
    ServiceClient serviceClient;
    AthleteClientService athleteService;

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
        // Set service classes
        // Create the service client
        serviceClient = new ServiceClient();

        // Create the athlete service
        athleteService = new AthleteClientService(serviceClient, LocalServer.IP_ADDRESS);

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

        // Create user with fields from the UI
        final Athlete athlete = new Athlete();
        athlete.setFirstName(textFieldName.getText().toString());
        athlete.setLastName(textFieldName.getText().toString());
        athlete.setEmail(textFieldEmail.getText().toString());
        athlete.setPassword(textFieldPassword.getText().toString());

        // Set the UserType (only needed if doing the baseUser approach
        // TODO Determine if the baseUser approach will be used
        if(radioButtonAthlete.isChecked()) {

        }

        // Execute the registration task
        final RegistrationTask registrationTask = new RegistrationTask(athlete);
        registrationTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private Athlete localAthlete;

        RegistrationTask(final Athlete athlete) {
            localAthlete = athlete;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "Athlete: " + localAthlete);

            // Run the service
            Athlete dbResult = null;
            if(athleteService != null) {
                dbResult = athleteService.register(localAthlete);
            } else {
                Log.e("doInBackground", "AthleteService is null");
            }

            Log.d("doInBackground", "dbResult: " + dbResult);

            // Check if an object was returned
            if(dbResult != null) {
                result = true;
            } else {
                Log.w("doInBackground", "dbResult is null");
            }

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
