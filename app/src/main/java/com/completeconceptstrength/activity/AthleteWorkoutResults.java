package com.completeconceptstrength.activity;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.exercise.impl.MainLiftInstance;
import completeconceptstrength.model.exercise.impl.MainLiftSet;
import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.PrescriptionInstanceClientService;

public class AthleteWorkoutResults extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionInstanceClientService prescriptionService;
    PrescriptionInstance prescriptionInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_workout_results);

        Bundle extras = getIntent().getExtras();
        long prescriptionResultID = extras.getLong("prescription");

        Log.i("onCreate", Long.toString(prescriptionResultID));

        final GetPrescriptionInstance getPrescriptionInstance = new GetPrescriptionInstance(prescriptionResultID);
        getPrescriptionInstance.execute((Void) null);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        prescriptionService = globalContext.getPrescriptionInstanceClientService();

        final TextView sleepValue = (TextView) findViewById(R.id.sleepValue);
        SeekBar sleepSB = (SeekBar) findViewById(R.id.sleepBar);
        sleepSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sleepValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView stressValue = (TextView) findViewById(R.id.stressValue);
        SeekBar stressSB = (SeekBar) findViewById(R.id.stressBar);
        stressSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stressValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView motivationValue = (TextView) findViewById(R.id.motivationValue);
        SeekBar motivationSB = (SeekBar) findViewById(R.id.motivationBar);
        motivationSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                motivationValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView nutritionValue = (TextView) findViewById(R.id.nutritionValue);
        SeekBar nutritionSB = (SeekBar) findViewById(R.id.nutritionBar);
        nutritionSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                nutritionValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final TextView sorenessValue = (TextView) findViewById(R.id.sorenessValue);
        SeekBar sorenessSB = (SeekBar) findViewById(R.id.sorenessBar);
        sorenessSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sorenessValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void addPrescriptionToView(){
        TableLayout setTable = (TableLayout) findViewById(R.id.setTable);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        //setTable.setLayoutParams(tableParams);

        TableRow row1 = new TableRow(this);
        row1.setLayoutParams(tableParams);

        TextView prescriptionDate = new TextView(this);
        prescriptionDate.setText(prescriptionInstance.getDateAssigned().toString());
        row1.addView(prescriptionDate);

        prescriptionInstance.getCoach().getFirstName();
        prescriptionInstance.getPrescriptionName();

        Log.i("addprescription", "Number sets " + Integer.toString(prescriptionInstance.getRecordedSets().size()));
        for(int i = 0; i < prescriptionInstance.getRecordedSets().size(); i++){
            TableRow row2 = new TableRow(this);
            row2.setLayoutParams(tableParams);

            MainLiftSet set = prescriptionInstance.getRecordedSets().get(i);
            String liftName = set.getMainLiftDefinition().getName();

            TextView liftNameTV = new TextView(this);
            liftNameTV.setText(liftName);
            liftNameTV.setTypeface(null, Typeface.BOLD);
            liftNameTV.setTextSize(14);
            row2.addView(liftNameTV);
            setTable.addView(row2);



            Log.i("addprescription", "set Row children " + row2.getChildCount() );
            Log.i("addprescription", "Set" + i + "Number lifts " + Integer.toString(set.getMainLifts().size()));

            for (int j = 0; j < set.getMainLifts().size(); j++) {
                TableRow row3 = new TableRow(this);
                row3.setLayoutParams(tableParams);

                MainLiftInstance liftInstance = set.getMainLifts().get(j);
                int reps = liftInstance.getPerformedRepetitions();

                Log.i("addprescription", "Lift " + j + " Number reps " + Integer.toString(reps));

                double weight = liftInstance.getPerformedWeight();

                Log.i("addprescription", "Lift " + j + " weight " + Double.toString(weight));

                TextView repText = new TextView(this);
                repText.setText("Reps:");
                repText.setWidth(20);

                EditText liftReps = new EditText(this);
                liftReps.setText(Integer.toString(reps));

                TextView wtText = new TextView(this);
                wtText.setText("Weight:");

                EditText liftWeight = new EditText(this);
                liftWeight.setText(Double.toString(weight));

                TextView pounds = new TextView(this);
                pounds.setText("lbs");

                row3.addView(repText);
                row3.addView(liftReps);
                row3.addView(wtText);
                row3.addView(liftWeight);
                row3.addView(pounds);

                setTable.addView(row3);
                Log.i("addprescription", "lift Row children " + row3.getChildCount());
            }

        }

        SeekBar sleepSB = (SeekBar) findViewById(R.id.sleepBar);
        sleepSB.setProgress(prescriptionInstance.getWellnessSleep());

        SeekBar stressSB = (SeekBar) findViewById(R.id.stressBar);
        stressSB.setProgress(prescriptionInstance.getWellnessStress());

        SeekBar motivationSB = (SeekBar) findViewById(R.id.motivationBar);
        motivationSB.setProgress(prescriptionInstance.getWellnessMotivation());

        SeekBar nutritionSB = (SeekBar) findViewById(R.id.nutritionBar);
        nutritionSB.setProgress(prescriptionInstance.getWellnessNutrition());

        SeekBar sorenessSB = (SeekBar) findViewById(R.id.sorenessBar);
        sorenessSB.setProgress(prescriptionInstance.getWellnessSoreness());
    }

    public void submitWorkoutResults(View v){
        SeekBar sleepSB = (SeekBar) findViewById(R.id.sleepBar);
        int sleepRating = sleepSB.getProgress();

        SeekBar motivationSB = (SeekBar) findViewById(R.id.motivationBar);
        int motivationRating = motivationSB.getProgress();

        SeekBar nutritionSB = (SeekBar) findViewById(R.id.nutritionBar);
        int nutritionRating = nutritionSB.getProgress();

        SeekBar sorenessSB = (SeekBar) findViewById(R.id.sorenessBar);
        int sorenessRating = sorenessSB.getProgress();

        SeekBar stressSB = (SeekBar) findViewById(R.id.stressBar);
        int stressRating = stressSB.getProgress();

        prescriptionInstance.setWellnessSleep(sleepRating);
        prescriptionInstance.setWellnessMotivation(motivationRating);
        prescriptionInstance.setWellnessNutrition(nutritionRating);
        prescriptionInstance.setWellnessSoreness(sorenessRating);
        prescriptionInstance.setWellnessStress(stressRating);

        TableLayout setTable = (TableLayout) findViewById(R.id.setTable);

        int setIndex = 0;
        int liftInstanceIndex = 0;
        for(int i = 0; i < setTable.getChildCount(); i++){
            TableRow row = (TableRow) setTable.getChildAt(i);
            if(row.getChildCount() == 1 && i > 0){
                setIndex++;
                liftInstanceIndex = 0;
                continue;
            }
            else if(row.getChildCount() > 1){
                EditText actualReps = (EditText) row.getChildAt(1);
                EditText actualWt = (EditText) row.getChildAt(3);

                int reps = Integer.valueOf(actualReps.getText().toString());
                float wt = Float.valueOf(actualWt.getText().toString());

                prescriptionInstance.getRecordedSets().get(setIndex).getMainLifts().get(liftInstanceIndex).setPerformedRepetitions(reps);
                prescriptionInstance.getRecordedSets().get(setIndex).getMainLifts().get(liftInstanceIndex).setPerformedWeight(wt);

                liftInstanceIndex++;
            }
        }

        final SetPrescriptionResult setPrescriptionResult = new SetPrescriptionResult(user, prescriptionInstance);
        setPrescriptionResult.execute((Void) null);
    }

    public class SetPrescriptionResult extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private PrescriptionInstance prescriptionInstance;

        SetPrescriptionResult(final User user, PrescriptionInstance prescriptionInstance){
            localUser = user;
            this.prescriptionInstance = prescriptionInstance;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to get connections: " + localUser);

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
                result = prescriptionService.postResults(localUser.getId(), prescriptionInstance);
            } else {
                Log.e("doInBackground", "prescriptionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = prescriptionService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error setting user prescription results with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Set user prescription results response is null");
                }
            }

            return result;
        }
    }

    public class GetPrescriptionInstance extends AsyncTask<Void, Void, Boolean> {

        private long prescriptionID;

        GetPrescriptionInstance(long prescriptionID){
            this.prescriptionID = prescriptionID;
        }

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
                prescriptionInstance = prescriptionService.fillAssignedWeight(prescriptionID);

                if(prescriptionInstance != null){
                    Log.i("doInBackground", "Prescription instance is not null :) ");

                    if(prescriptionInstance.getRecordedSets() == null){
                        Log.i("doInBackground", "RecordedSets is null :( ");
                    }
                    else {
                        Log.i("doInBackground", "RecordedSets is not null :) ");
                    }

                    result = true;
                }
            } else {
                Log.e("doInBackground", "prescriptionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = prescriptionService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user prescription instance with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user prescription instance response is null");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success){
                setTitle(prescriptionInstance.getPrescriptionName());

                TextView coachName = (TextView) findViewById(R.id.coachName);
                coachName.setText("Assigned by : " + prescriptionInstance.getCoach().getFirstName() + " " + prescriptionInstance.getCoach().getLastName());

                TextView rxDate = (TextView) findViewById(R.id.rxDate);
                rxDate.setText(prescriptionInstance.getDateAssigned().toString());

                addPrescriptionToView();
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
