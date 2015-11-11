package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.completeconceptstrength.application.SetAdapter;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import completeconceptstrength.model.base.impl.CustomType;
import completeconceptstrength.model.exercise.impl.MainLiftDefinition;
import completeconceptstrength.model.exercise.impl.MainLiftInstance;
import completeconceptstrength.model.exercise.impl.MainLiftSet;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.MainLiftDefinitionClientService;
import completeconceptstrength.services.impl.MainLiftSetClientService;

public class CustomSetsActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    MainLiftDefinitionClientService liftService;
    MainLiftSetClientService setService;

    List<MainLiftDefinition> customLifts;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_sets);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        liftService = globalContext.getMainLiftDefinitionClientService();
        setService = globalContext.getMainLiftSetClientService();

        final GetLiftDefinitions getLiftsTask = new GetLiftDefinitions(user);
        getLiftsTask.execute((Void) null);
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

    public void submitCustomSet(View v){
        MainLiftDefinition lift = getSetLift();
        List<MainLiftInstance> mainLiftInstanceList = getRepetitions();

        EditText setNameET = (EditText) findViewById(R.id.setName);
        String setName = setNameET.getText().toString();

        EditText setDescriptionET = (EditText) findViewById(R.id.setDescription);
        String setDescription = setDescriptionET.getText().toString();

        final SetSetDefinitions setSetsTask = new SetSetDefinitions(user, lift, mainLiftInstanceList, setName, setDescription);
        setSetsTask.execute((Void) null);

        Intent intent = new Intent(this, CoachWorkoutsSets.class);
        startActivity(intent);
    }

    public MainLiftDefinition getSetLift(){
        MainLiftDefinition mainLiftDefinition = null;

        Spinner liftSpinner = (Spinner) findViewById(R.id.liftSpinner);
        String liftName = liftSpinner.getSelectedItem().toString();

        int position = 0;
        while(mainLiftDefinition == null && position < customLifts.size()){
            if(customLifts.get(position).getName().equals(liftName)){
                mainLiftDefinition = customLifts.get(position);
            }
            position++;
        }

        return mainLiftDefinition;
    }

    public List<MainLiftInstance> getRepetitions(){
        TableLayout repTable = (TableLayout) findViewById(R.id.the_table);

        List<MainLiftInstance> mainLiftInstanceList = new ArrayList<MainLiftInstance>();

        for(int i = 0; i < repTable.getChildCount(); i++){
            TableRow repRow = (TableRow) repTable.getChildAt(i);

            EditText numReps = (EditText) repRow.getChildAt(0);
            EditText oneRM = (EditText) repRow.getChildAt(1);

            int reps = Integer.valueOf(numReps.getText().toString());
            int repMax = Integer.valueOf(oneRM.getText().toString());

            MainLiftInstance currSet = new MainLiftInstance(repMax, reps);
            mainLiftInstanceList.add(currSet);
        }

        return mainLiftInstanceList;
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

    public class SetSetDefinitions extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private  MainLiftDefinition lift;
        private List<MainLiftInstance> listMainLiftInstance;
        private String setName;
        private String setDescription;
        private MainLiftSet customSet;

        SetSetDefinitions(final User user, MainLiftDefinition lift, List<MainLiftInstance> listMainLiftInstance, String setName, String setDescription) {
            localUser = user;
            this.lift = lift;
            this.listMainLiftInstance = listMainLiftInstance;
            this.setName = setName;
            this.setDescription = setDescription;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to set sets: " + localUser);

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
                customSet = setService.addCustomObject(new MainLiftSet
                        (lift, listMainLiftInstance, setName, CustomType.CUSTOM), localUser.getId());

                if(customSet != null) {
                    customSet.setDescription(setDescription);
                    result = true;
                }
            } else {
                Log.e("doInBackground", "MainLiftSetService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = setService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user sets with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user sets response is null");
                }
            }

            return result;
        }
    }


}
