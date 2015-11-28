package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.exercise.impl.MainLiftDefinition;
import completeconceptstrength.model.exercise.impl.MainLiftSet;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftDefinitionClientService;
import completeconceptstrength.services.impl.MainLiftSetClientService;

public class CustomPrescriptionActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftDefinitionClientService liftService;
    MainLiftSetClientService setService;

    List<MainLiftDefinition> customLifts;
    ArrayAdapter<String> liftAdapter;

    List<MainLiftSet> customSets;
    ArrayAdapter<String> setAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_prescription);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        liftService = globalContext.getMainLiftDefinitionClientService();
        setService = globalContext.getMainLiftSetClientService();

        final GetLiftDefinitions getLiftsTask = new GetLiftDefinitions(user);
        getLiftsTask.execute((Void) null);
        final GetSetDefinitions getSetsTask = new GetSetDefinitions(user);
        getSetsTask.execute((Void) null);
    }

    public class GetLiftDefinitions extends AsyncTask<Void, Void, Boolean> {

        private User localUser;

        GetLiftDefinitions(final User user) {
            localUser = user;
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
                customLifts = liftService.getCustomObjectsByOwner(localUser.getId());

                if(customLifts != null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "userConnectionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = liftService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user lifts with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user lifts response is null");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success && customLifts != null && !customLifts.isEmpty()){
                Spinner dropdown = (Spinner)findViewById(R.id.liftSpinner);
                liftAdapter = new ArrayAdapter<String>(CustomPrescriptionActivity.this, android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(liftAdapter);

                if(customLifts != null && !customLifts.isEmpty()) {
                    for (final MainLiftDefinition l : customLifts) {
                        liftAdapter.add(l.getName());
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

    public class GetSetDefinitions extends AsyncTask<Void, Void, Boolean> {

        private User localUser;

        GetSetDefinitions(final User user) {
            localUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to get connections: " + localUser);

            // Set service class
            if(setService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                setService = globalContext.getMainLiftSetClientService();
            }

            // Run the service
            if(setService != null) {
                customSets = setService.getCustomObjectsByOwner(localUser.getId());

                if(customSets != null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "userConnectionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = setService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user lifts with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user lifts response is null");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success){
                Spinner dropdown = (Spinner)findViewById(R.id.setSpinner);
                setAdapter = new ArrayAdapter<String>(CustomPrescriptionActivity.this, android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(setAdapter);

                if(customSets != null && !customSets.isEmpty()) {
                    for (final MainLiftSet s : customSets) {
                        setAdapter.add(s.getName());
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }
}
