package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.CoachPrescriptionsAdapter;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.exercise.impl.PrescriptionDefinition;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.PrescriptionDefinitionClientService;

public class CoachWorkoutsPrescriptions extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionDefinitionClientService rxService;

    List<PrescriptionDefinition> customRx;
    CoachPrescriptionsAdapter rxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_workouts_prescriptions);
        setTitle("Prescriptions");

        globalContext = (GlobalContext) getApplicationContext();
        user = globalContext.getLoggedInUser();
        rxService = globalContext.getPrescriptionDefinitionClientService();

        final GetRxDefinitions getRxTask = new GetRxDefinitions(user);
        getRxTask.execute((Void) null);
    }

    public void openNewRx(View v){
        Intent intent = new Intent(this, CustomPrescriptionActivity.class);
        startActivity(intent);
    }

    public class GetRxDefinitions extends AsyncTask<Void, Void, Boolean> {

        private User localUser;

        GetRxDefinitions(final User user) {
            localUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to get connections: " + localUser);

            // Set service class
            if(rxService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                rxService = globalContext.getPrescriptionDefinitionClientService();
            }

            // Run the service
            if(rxService != null) {
                customRx = rxService.getCustomObjectsByOwner(localUser.getId());

                if(customRx != null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "userConnectionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = rxService.getLastResponse();
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
                rxAdapter = new CoachPrescriptionsAdapter(CoachWorkoutsPrescriptions.this,
                        R.layout.view_prescription_entry_item);

                ListView rxList = (ListView) findViewById(R.id.rxList);
                rxList.setAdapter(rxAdapter);

                if(customRx != null && !customRx.isEmpty()) {
                    for (final PrescriptionDefinition p : customRx) {
                        rxAdapter.add(p);
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
