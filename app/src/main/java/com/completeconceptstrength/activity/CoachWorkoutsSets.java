package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.SetAdapter;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.exercise.impl.MainLiftSet;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftSetClientService;

public class CoachWorkoutsSets extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftSetClientService setService;

    SetAdapter setAdapter;
    List<MainLiftSet> customSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_workouts_sets);

        setTitle("Sets");

        globalContext = (GlobalContext) getApplicationContext();
        user = globalContext.getLoggedInUser();
        setService = globalContext.getMainLiftSetClientService();

        final GetSetDefinitions getSetsTask = new GetSetDefinitions(user);
        getSetsTask.execute((Void) null);
    }

    public void openNewSet(View v){
        Intent intent = new Intent(this, CustomSetsActivity.class);
        startActivity(intent);
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
                setAdapter = new SetAdapter(CoachWorkoutsSets.this,
                        R.layout.set_entry_item);

                ListView setList = (ListView) findViewById(R.id.setList);
                setList.setAdapter(setAdapter);

                if(customSets != null && !customSets.isEmpty()) {
                    for (final MainLiftSet s : customSets) {
                        setAdapter.add(s);
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }


}
