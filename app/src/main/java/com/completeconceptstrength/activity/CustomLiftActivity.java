package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.base.impl.CustomType;
import completeconceptstrength.model.exercise.impl.MainLiftDefinition;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftDefinitionClientService;

public class CustomLiftActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftDefinitionClientService liftService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_lift);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        liftService = globalContext.getMainLiftDefinitionClientService();

    }

    public void submitCustomLift(View v){
        EditText liftNameET = (EditText) findViewById(R.id.liftName);
        EditText liftCategoryET = (EditText) findViewById(R.id.liftCategory);
        EditText liftDescriptionET = (EditText) findViewById(R.id.liftDescription);

        String liftName = liftNameET.getText().toString();
        String liftCategory = liftCategoryET.getText().toString();
        String liftDescription = liftDescriptionET.getText().toString();

        final SetLiftDefinitions getLiftsTask = new SetLiftDefinitions(user, liftName, liftCategory, liftDescription);
        getLiftsTask.execute((Void) null);

        Intent intent = new Intent(this, CoachWorkoutsLifts.class);
        startActivity(intent);
    }

    public class SetLiftDefinitions extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private String liftName;
        private String liftCategory;
        private String liftDescription;
        private MainLiftDefinition newLift;

        SetLiftDefinitions(final User user, String liftName, String liftCategory, String liftDescription) {
            localUser = user;
            this.liftName = liftName;
            this.liftCategory = liftCategory;
            this.liftDescription = liftDescription;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to get connections: " + localUser);

            // Set service class
            if(liftService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                liftService = globalContext.getMainLiftDefinitionClientService();
            }

            // Run the service
            if(liftService != null) {
                newLift = liftService.addCustomObject(new MainLiftDefinition(liftName, liftCategory, CustomType.CUSTOM), localUser.getId());

                if(newLift != null) {
                    newLift.setDescription(liftDescription);
                    result = true;
                }
            } else {
                Log.e("doInBackground", "liftService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = liftService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error setting user lifts with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Set user lifts response is null");
                }
            }

            return result;
        }
    }

}
