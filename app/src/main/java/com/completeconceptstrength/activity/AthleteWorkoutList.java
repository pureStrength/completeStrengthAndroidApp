package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.PrescriptionsAdapter;

import org.apache.http.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.PrescriptionInstanceClientService;

public class AthleteWorkoutList extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionInstanceClientService prescriptionService;
    List<PrescriptionInstance> prescriptions;
    ListView rxList;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workout_list);

        date = null;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("prescriptionDate")) {
                Long time = extras.getLong("prescriptionDate");
                date = new Date();
                date.setTime(time);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d");
            setTitle(sdf.format(date));
        }
        else{
            setTitle("Prescriptions");
        }

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
                    if(date == null || (date != null && rxMidnightDate(p.getDateAssigned()).equals(date))) {
                        rxAdapter.add(p);
                    }
                }

                if(rxAdapter.isEmpty()){
                    View rl = findViewById(R.id.layout_workout_list);
                    TextView tv = new TextView(AthleteWorkoutList.this);

                    if(date != null){
                        tv.setText("No prescriptions are available for this date.");
                    }
                    else {
                        tv.setText("You currently have no prescriptions. Try connecting with a trainer to get started.");
                    }

                    ((RelativeLayout)rl).addView(tv);
                }
            }
            else {

                View rl = findViewById(R.id.layout_workout_list);
                TextView tv = new TextView(AthleteWorkoutList.this);

                if(date != null){
                    tv.setText("No prescriptions are available for this date.");
                }
                else {
                    tv.setText("You currently have no prescriptions. Try connecting with a trainer to get started.");
                }

                ((RelativeLayout)rl).addView(tv);

                Log.e("onPostExecute", "Execute unsuccessful or no results found");
            }
        }
    }

    public Date rxMidnightDate(Date rxDate){
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(rxDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

}
