package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.completeconceptstrength.application.LiftAdapter;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import completeconceptstrength.model.exercise.impl.MainLiftDefinition;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftDefinitionClientService;

public class CustomSetsActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftDefinitionClientService liftService;

    List<MainLiftDefinition> customLifts;
    ArrayList<String> liftNames;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_sets);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        liftService = globalContext.getMainLiftDefinitionClientService();

        liftNames = new ArrayList<String>();

        final GetLiftDefinitions getLiftsTask = new GetLiftDefinitions(user);
        getLiftsTask.execute((Void) null);
    }

    public void setLiftNames(List<MainLiftDefinition> liftList){

        if(liftList == null || !liftList.isEmpty()){
            return;
        }

        for(MainLiftDefinition l : liftList){
            liftNames.add(l.getName());
        }
    }

    public void addRep(View v){
        TableLayout table = (TableLayout) findViewById(R.id.the_table);
        TableRow row = new TableRow(this);

        EditText et1 = new EditText(this);
        et1.setHint("Number of Reps");

        EditText et2 = new EditText(this);
        et2.setHint("% 1RM");

        Button b = new Button(this);
        b.setText("Remove");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View bRow = (View) v.getParent();
                ViewGroup container = (ViewGroup) bRow.getParent();
                container.removeView(bRow);
                container.invalidate();
            }
        });

        row.addView(et1);
        row.addView(et2);
        row.addView(b);

        table.addView(row);
    }

    public void clearTable(View v){
        TableLayout table = (TableLayout)findViewById(R.id.the_table);
        table.removeAllViews();
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
            if(success && customLifts != null && !customLifts.isEmpty()){
                //setLiftNames(customLifts);

                Spinner dropdown = (Spinner)findViewById(R.id.liftSpinner);
                adapter = new ArrayAdapter<String>(CustomSetsActivity.this, android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);

                if(customLifts != null && !customLifts.isEmpty()) {
                    for (final MainLiftDefinition l : customLifts) {
                        adapter.add(l.getName());
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
