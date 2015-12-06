package com.completeconceptstrength.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.exercise.impl.AccessoryLiftInstance;
import completeconceptstrength.model.exercise.impl.MainLiftInstance;
import completeconceptstrength.model.exercise.impl.MainLiftSet;
import completeconceptstrength.model.exercise.impl.PreferenceUnitType;
import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserType;
import completeconceptstrength.services.impl.PrescriptionInstanceClientService;

public class AthleteWorkoutResults extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionInstanceClientService prescriptionService;
    PrescriptionInstance prescriptionInstance;
    boolean useKGUnits;

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

        useKGUnits = user.getPreferenceUnitType().equals(PreferenceUnitType.METRIC);

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

        if(user.getUserType().equals(UserType.COACH)){
            Button resultButton = (Button) findViewById(R.id.resultButton);
            resultButton.setEnabled(false);
            resultButton.setVisibility(View.INVISIBLE);
        }
    }

    public void addPrescriptionToView(){
        addSetsToView();

        addAccessoryLiftsToView();

        TextView abFocus = (TextView) findViewById(R.id.abFocus);
        abFocus.setText("Abdominal Focus: " + prescriptionInstance.getAbdominalFocus().getType());

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

    public void addSetsToView(){

        if(prescriptionInstance.getRecordedSets() == null || prescriptionInstance.getRecordedSets().isEmpty()){
            return;
        }

        TableLayout setTable = (TableLayout) findViewById(R.id.setTable);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

        for(int i = 0; i < prescriptionInstance.getRecordedSets().size(); i++){
            TableRow row1 = new TableRow(this);
            row1.setLayoutParams(tableParams);

            MainLiftSet set = prescriptionInstance.getRecordedSets().get(i);
            String liftName = set.getMainLiftDefinition().getName();

            TextView liftNameTV = new TextView(this);
            liftNameTV.setText(liftName);
            liftNameTV.setTypeface(null, Typeface.BOLD);
            liftNameTV.setTextSize(18);
            row1.addView(liftNameTV);

            setTable.addView(row1);

            if(set.getMainLifts() == null || set.getMainLifts().isEmpty()){
                return;
            }

            for (int j = 0; j < set.getMainLifts().size(); j++) {
                TableRow row2 = new TableRow(this);
                row2.setLayoutParams(tableParams);

                MainLiftInstance liftInstance = set.getMainLifts().get(j);
                int reps = liftInstance.getPerformedRepetitions();
                double weight = liftInstance.getPerformedWeight();

                TextView repText = new TextView(this);
                repText.setText("Reps:");
                repText.setWidth(20);

                EditText liftReps = new EditText(this);
                liftReps.setText(Integer.toString(reps));

                TextView wtText = new TextView(this);
                wtText.setText("Wt:");

                EditText liftWeight = new EditText(this);
                TextView pounds = new TextView(this);

                if(useKGUnits){
                    liftWeight.setText(Integer.toString((int)(convertToKG(weight))));
                    pounds.setText("kg");
                }
                else {
                    liftWeight.setText(Integer.toString((int)(Math.ceil(weight))));
                    pounds.setText("lbs");
                }

                row2.addView(repText);
                row2.addView(liftReps);
                row2.addView(wtText);
                row2.addView(liftWeight);
                row2.addView(pounds);

                if(user.getUserType().equals(UserType.COACH)){
                    TextView predictedORMTV = new TextView(this);
                    predictedORMTV.setText("New 1RM:" + predictORM(weight, reps));
                    row2.addView(predictedORMTV);
                }

                setTable.addView(row2);
            }
        }
    }

    public int convertToKG(double weightInKG){
        return (int) Math.ceil(weightInKG/2.2);
    }

    public int convertToLB(double weightInLBS){
        return (int) Math.ceil(weightInLBS*2.2);
    }

    public void addAccessoryLiftsToView(){
        TableLayout accessoryTable = (TableLayout) findViewById(R.id.accessoryTable);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

        TableRow row1 = new TableRow(this);
        row1.setLayoutParams(tableParams);

        TextView liftNameColTV = new TextView(this);
        TextView categoryColTV = new TextView(this);
        TextView setsColTV = new TextView(this);
        TextView repsColTV = new TextView(this);

        liftNameColTV.setText("Lift");
        categoryColTV.setText("Cat.");
        setsColTV.setText("Sets");
        repsColTV.setText("Reps");

        row1.addView(liftNameColTV);
        row1.addView(categoryColTV);
        row1.addView(setsColTV);
        row1.addView(repsColTV);

        accessoryTable.addView(row1);

        List<AccessoryLiftInstance> accessoryLiftInstanceList = prescriptionInstance.getAccessoryLifts();

        for(int accessoryLift = 0; accessoryLift < accessoryLiftInstanceList.size(); accessoryLift++){
            TableRow row2 = new TableRow(this);
            row2.setLayoutParams(tableParams);

            AccessoryLiftInstance accessoryLiftInstance = accessoryLiftInstanceList.get(accessoryLift);

            String liftName = accessoryLiftInstance.getMainLiftDefinition().getName();
            String category = accessoryLiftInstance.getCategory();
            String sets = Integer.toString(accessoryLiftInstance.getAssignedSets());
            String reps = accessoryLiftInstance.getAssignedRepetitions();

            TextView liftNameTV = new TextView(this);
            TextView categoryTV = new TextView(this);
            TextView setsTV = new TextView(this);
            TextView repsTV = new TextView(this);

            liftNameTV.setText(liftName);
            categoryTV.setText(category);
            setsTV.setText(sets);
            repsTV.setText(reps);

            liftNameTV.setTypeface(null, Typeface.BOLD);
            liftNameTV.setTextSize(14);
            categoryTV.setTextSize(14);
            setsTV.setTextSize(14);
            repsTV.setTextSize(14);

            row2.addView(liftNameTV);
            row2.addView(categoryTV);
            row2.addView(setsTV);
            row2.addView(repsTV);

            accessoryTable.addView(row2);
        }
    }

    public String predictORM(double weight, int reps){

        if(useKGUnits){
            weight = convertToKG(weight);
        }

        int predictedORM = (int) (weight/(1.013-(0.0267123*reps)));

        String predictedORMString = Integer.toString(predictedORM);
        return predictedORMString;
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

                if(useKGUnits){
                    wt = convertToLB(wt);
                }

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

                TextView completed = (TextView) findViewById(R.id.completed);
                String completeStatus = "NO";

                if(prescriptionInstance.getWasPerformed()){
                    completeStatus = "YES";
                }

                completed.setText("Completed: " + completeStatus);

                addPrescriptionToView();
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
