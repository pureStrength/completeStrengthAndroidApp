package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.SetAdapter;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.exercise.impl.MainLiftInstance;
import completeconceptstrength.model.exercise.impl.MainLiftSet;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftSetClientService;

public class SetViewActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftSetClientService setService;
    MainLiftSet currentSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);
        setTitle("Set Details");

        Bundle extras = getIntent().getExtras();
        Long setID = extras.getLong("setID");

        globalContext = (GlobalContext) getApplicationContext();
        user = globalContext.getLoggedInUser();
        setService = globalContext.getMainLiftSetClientService();

        final GetSetDefinition getSetDefinition = new GetSetDefinition(setID);
        getSetDefinition.execute((Void) null);
    }

    public class GetSetDefinition extends AsyncTask<Void, Void, Boolean> {

        private long setID;

        GetSetDefinition(long setID) {
            this.setID = setID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

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
                currentSet = setService.get(setID);

                if(currentSet != null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "main lift set service is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = setService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting lift with status code: " + response.getStatusLine().getStatusCode());
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
                setLayout();
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

    public void setLayout() {
        TextView setName = (TextView) findViewById(R.id.setName);
        setName.setText(currentSet.getName());

        TextView setType = (TextView) findViewById(R.id.setType);
        setType.setText(currentSet.getCustomType().getType());

        TextView setCategory = (TextView) findViewById(R.id.setCategory);
        setCategory.setText(currentSet.getCategory());

        TextView setDescription = (TextView) findViewById(R.id.setDescription);
        setDescription.setText(currentSet.getDescription());

        TextView liftType = (TextView) findViewById(R.id.liftType);
        liftType.setText(currentSet.getMainLiftDefinition().getName());

        setTable();
    }

    public void setTable(){
        TableLayout setTable = (TableLayout) findViewById(R.id.setTable);

        for (int j = 0; j < currentSet.getMainLifts().size(); j++) {
            TableRow tableRow = new TableRow(this);

            MainLiftInstance liftInstance = currentSet.getMainLifts().get(j);
            int reps = liftInstance.getAssignedRepetitions();
            double weight = liftInstance.getAssignedPercentOfOneRepMax();

            TextView repText = new TextView(this);
            repText.setText("Reps:");
            repText.setWidth(20);

            EditText liftReps = new EditText(this);
            liftReps.setText(Integer.toString(reps));

            TextView wtText = new TextView(this);
            wtText.setText("% 1RM: ");
            wtText.setPadding(20, 0, 0, 0);

            EditText liftWeight = new EditText(this);
            liftWeight.setText(Double.toString(weight));

            tableRow.addView(repText);
            tableRow.addView(liftReps);
            tableRow.addView(wtText);
            tableRow.addView(liftWeight);

            setTable.addView(tableRow);
        }
    }
}
