package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.exercise.impl.MainLiftDefinition;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftDefinitionClientService;

public class CoachWorkoutsLifts extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftDefinitionClientService liftService;
    LiftAdapter liftAdapter;

    List<MainLiftDefinition> customLifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_workouts_lifts);
        setTitle("Lifts");

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        liftService = globalContext.getMainLiftDefinitionClientService();

        final GetLiftDefinitions getLiftsTask = new GetLiftDefinitions(user);
        getLiftsTask.execute((Void) null);
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
            if(success){
                liftAdapter = new LiftAdapter(CoachWorkoutsLifts.this,
                        R.layout.lift_entry_item);

                ListView liftList = (ListView) findViewById(R.id.liftList);
                liftList.setAdapter(liftAdapter);

                if(customLifts != null && !customLifts.isEmpty()) {
                    for (final MainLiftDefinition l : customLifts) {
                        liftAdapter.add(l);
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }


}
