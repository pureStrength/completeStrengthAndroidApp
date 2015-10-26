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

import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.services.impl.PrescriptionInstanceClientService;

public class AthleteWorkoutList extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionInstanceClientService prescriptionService;
    List<PrescriptionInstance> prescriptions;
    ListView rxList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workout_list);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        prescriptionService = globalContext.getPrescriptionInstanceClientService();

        rxList = (ListView) findViewById(R.id.prescriptionList);

        final GetPrescriptionResults getPresTask = new GetPrescriptionResults(user);
        getPresTask.execute((Void) null);
    }

    public class GetPrescriptionResults extends AsyncTask<Void, Void, Boolean> {
        private User localUser;

        GetPrescriptionResults(final User user) {localUser = user;}

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            Log.i("doInBackground", "User currently searching: " + localUser);

            // Set service class
            if(prescriptionService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                prescriptionService = globalContext.getPrescriptionInstanceClientService();
            }

            // Run the service
            if(prescriptionService != null) {
                //String query = searchView.getQuery().toString();
                prescriptions = prescriptionService.getByAthlete(localUser.getId());

                Log.i("doInBackground", String.valueOf(prescriptions.size()));

                if(prescriptions!= null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "PrescriptionInstanceService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = prescriptionService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting prescription results with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get prescription results response is null");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success && prescriptions != null && !prescriptions.isEmpty()){
                PrescriptionsAdapter rxAdapter = new PrescriptionsAdapter(AthleteWorkoutList.this,
                        R.layout.prescription_entry_item);

                rxList = (ListView) findViewById(R.id.prescriptionList);
                rxList.setTextFilterEnabled(true);
                rxList.setAdapter(rxAdapter);

                for(final PrescriptionInstance p : prescriptions) {
                    rxAdapter.add(p);
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful or no results found");
            }
        }
    }

}
