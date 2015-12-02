package com.completeconceptstrength.activity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
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
import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.PrescriptionInstanceClientService;

public class PrescriptionViewActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    PrescriptionInstanceClientService prescriptionService;
    PrescriptionInstance prescriptionInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_view);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        prescriptionService = globalContext.getPrescriptionInstanceClientService();

        Bundle extras = getIntent().getExtras();
        long prescriptionID = extras.getLong("prescriptionID");

        final GetPrescriptionInstance getPrescriptionInstance = new GetPrescriptionInstance(prescriptionID);
        getPrescriptionInstance.execute((Void) null);
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

                addPrescriptionToView();
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

    public void addPrescriptionToView(){
        addSetsToView();

        addAccessoryLiftsToView();

        TextView abFocus = (TextView) findViewById(R.id.abFocus);
        abFocus.setText("Abdominal Focus: " + prescriptionInstance.getAbdominalFocus().getType());
    }

    public void addSetsToView(){
        TableLayout setTable = (TableLayout) findViewById(R.id.setTable);

        for(int i = 0; i < prescriptionInstance.getRecordedSets().size(); i++){
            TableRow row1 = new TableRow(this);

            MainLiftSet set = prescriptionInstance.getRecordedSets().get(i);
            String liftName = set.getMainLiftDefinition().getName();

            TextView liftNameTV = new TextView(this);
            liftNameTV.setText(liftName);
            liftNameTV.setTypeface(null, Typeface.BOLD);
            liftNameTV.setTextSize(14);
            row1.addView(liftNameTV);
            setTable.addView(row1);

            for (int j = 0; j < set.getMainLifts().size(); j++) {
                TableRow row2 = new TableRow(this);

                MainLiftInstance liftInstance = set.getMainLifts().get(j);
                int reps = liftInstance.getAssignedRepetitions();
                double weight = liftInstance.getAssignedPercentOfOneRepMax();

                TextView repText = new TextView(this);
                repText.setText("Reps:");
                repText.setWidth(20);

                EditText liftReps = new EditText(this);
                liftReps.setText(Integer.toString(reps));

                TextView wtText = new TextView(this);
                wtText.setText("%1RM: ");
                wtText.setPadding(10, 0, 0, 0);

                EditText liftWeight = new EditText(this);
                liftWeight.setText(Double.toString(weight));

                row2.addView(repText);
                row2.addView(liftReps);
                row2.addView(wtText);
                row2.addView(liftWeight);

                setTable.addView(row2);
            }
        }
    }

    public void addAccessoryLiftsToView(){
        TableLayout accessoryTable = (TableLayout) findViewById(R.id.accessoryTable);

        TableRow row1 = new TableRow(this);

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
}
