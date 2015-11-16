package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.PrescriptionsAdapter;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.PrescriptionInstanceClientService;

public class AthleteWorkoutCalendar extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionInstanceClientService prescriptionService;
    List<PrescriptionInstance> prescriptions;
    List<Date> prescriptionDates;
    CaldroidFragment caldroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workout_calendar);
        setTitle("Prescription Calendar");

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        prescriptionService = globalContext.getPrescriptionInstanceClientService();

        prescriptionDates = new ArrayList<Date>();

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        //TODO these don't show up on calendar unless you change the month and then change it back
        final GetPrescriptionResults getPrescriptionResults = new GetPrescriptionResults(user);
        getPrescriptionResults.execute((Void) null);

        final CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                //if(prescriptionInstances != null && !prescriptionInstances.isEmpty()){
                    Intent intent = new Intent(AthleteWorkoutCalendar.this, AthleteWorkoutList.class);
                    intent.putExtra("prescriptionDate", date.getTime());
                    startActivity(intent);
                //}
            }
        };
        caldroidFragment.setCaldroidListener(caldroidListener);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();
    }

    public List<PrescriptionInstance> getPrescriptionsOnDate(Date date){
        List<PrescriptionInstance> prescriptionInstancesOnDate = new ArrayList<PrescriptionInstance>();

        return prescriptionInstancesOnDate;
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
            if(success){
                setCalendarDates();
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

    private void setCalendarDates() {
        for(int i = 0; i < prescriptions.size(); i++){
            if(prescriptions.get(i).getWasPerformed())
                caldroidFragment.setBackgroundResourceForDate(R.color.accent_material_dark, prescriptions.get(i).getDateAssigned());
            else
                caldroidFragment.setBackgroundResourceForDate(R.color.accent_material_light, prescriptions.get(i).getDateAssigned());
        }
        caldroidFragment.refreshView();
    }
}
