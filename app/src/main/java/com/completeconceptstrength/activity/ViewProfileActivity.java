package com.completeconceptstrength.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import completeconceptstrength.model.exercise.impl.OneRepMax;
import completeconceptstrength.model.exercise.impl.OneRepMaxChart;
import completeconceptstrength.model.exercise.impl.TrackEvent;
import completeconceptstrength.model.exercise.impl.TrackEventChart;
import completeconceptstrength.model.exercise.impl.TrackTime;
import completeconceptstrength.model.user.impl.AthleteProfile;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.UserClientService;

public class ViewProfileActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    UserClientService userClientService;
    User profileUser;
    AthleteProfile profileAthlete;
    HashMap<String, OneRepMaxChart> liftsByName;
    HashMap<String, TrackEventChart> eventsByName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        userClientService = globalContext.getUserClientService();

        Bundle extra = getIntent().getExtras();
        long profileViewID = extra.getLong("profileID");

        final GetUserProfileInfo getProfileTask = new GetUserProfileInfo(profileViewID);
        getProfileTask.execute((Void) null);
    }

    public class GetUserProfileInfo extends AsyncTask<Void, Void, Boolean>{

        private long userID;

        GetUserProfileInfo(final long userID){
            this.userID = userID;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User ID to get connections: " + userID);

            // Set service class
            if(userClientService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                userClientService = globalContext.getUserClientService();
            }

            // Run the service
            if(userClientService != null) {
                profileUser = userClientService.get(userID);
                profileAthlete = userClientService.getAthlete(userID).getAthleteProfile();

                if(profileAthlete!= null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "user client service is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                final HttpResponse response = userClientService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user profile with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user profile response is null");
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            setProfile();
        }
    }

    public void setProfile(){
        TextView nameTV = (TextView) findViewById(R.id.athleteName);
        TextView emailTV = (TextView) findViewById(R.id.athleteEmail);
        TextView orgTV = (TextView) findViewById(R.id.organization);
        TableLayout ORMTable = (TableLayout) findViewById(R.id.ORMTable);
        TableLayout timeTable = (TableLayout) findViewById(R.id.timeTable);

        setupGraphs();

        final Spinner dropdown = (Spinner)findViewById(R.id.ormSpinner);
        final Spinner dropdown2 = (Spinner)findViewById(R.id.ormSpinner2);
        final Spinner eventDropdown = (Spinner) findViewById(R.id.eventSpinner);

        nameTV.setText(profileUser.getFirstName() + " " + profileUser.getLastName());
        emailTV.setText(profileUser.getEmail());
        orgTV.setText(profileUser.getOrganization());

        List<OneRepMaxChart> ORMs = profileAthlete.getMostRecentOneRepMaxes();
        List<OneRepMaxChart> allORMs = profileAthlete.getOneRepMaxCharts();
        liftsByName = new HashMap<String, OneRepMaxChart>();

        Log.i("num lifts", "Recent ORMs: " + ORMs.size() + " All ORMs: " + allORMs.size());

        int i = 0;
        for(OneRepMaxChart O : ORMs){
            liftsByName.put(allORMs.get(i).getLiftName(), allORMs.get(i++));

            TableRow tr = new TableRow(this);

            TextView liftName = new TextView(this);
            TextView value = new TextView(this);
            TextView dateUpdated = new TextView(this);

            liftName.setText(O.getLiftName());
            liftName.setTextSize(18);
            liftName.setPadding(0, 0, 40, 0);

            String ORMvalue = Integer.toString(O.getMostRecentOneRepMax().getValue());

            if(ORMvalue != null) {
                value.setText(ORMvalue + " lbs");
                value.setTextSize(18);
                value.setPadding(0, 0, 40, 0);

                Date updateDate = O.getMostRecentOneRepMax().getDate();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/d");

                dateUpdated.setText("on " + sdf.format(updateDate));
                dateUpdated.setTextSize(18);
            }

            tr.addView(liftName);
            tr.addView(value);
            tr.addView(dateUpdated);

            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=10;
            int rightMargin=10;
            int bottomMargin = 2;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            tr.setLayoutParams(tableRowParams);

            ORMTable.addView(tr);
        }

        createGraph(ORMs.get(0).getOneRepMaxes(), ORMs.get(1).getOneRepMaxes());

        ArrayList<String> liftNames = new ArrayList<>(liftsByName.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, liftNames);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<OneRepMax> graphValues = liftsByName.get(dropdown.getSelectedItem().toString()).getOneRepMaxes();
                List<OneRepMax> graphValues2 = liftsByName.get(dropdown2.getSelectedItem().toString()).getOneRepMaxes();
                createGraph(graphValues, graphValues2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdown2.setAdapter(adapter);
        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<OneRepMax> graphValues = liftsByName.get(dropdown.getSelectedItem().toString()).getOneRepMaxes();
                List<OneRepMax> graphValues2 = liftsByName.get(dropdown2.getSelectedItem().toString()).getOneRepMaxes();
                createGraph(graphValues, graphValues2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<TrackEventChart> times = profileAthlete.getBestTrackTimes();
        List<TrackEventChart> allTimes = profileAthlete.getTrackEventCharts();
        eventsByName = new HashMap<String, TrackEventChart>();

        int j = 0;
        for(TrackEventChart t : times){
            eventsByName.put(allTimes.get(j).getEventName(), allTimes.get(j++));

            TableRow tr = new TableRow(this);

            TextView eventName = new TextView(this);
            TextView value = new TextView(this);
            TextView dateUpdated = new TextView(this);

            eventName.setText(t.getEventName());
            eventName.setTextSize(18);
            eventName.setPadding(0, 0, 20, 0);

            TrackTime eventValue = t.getBestTrackTime().getTrackTime();
            String trackTime = eventValue.getHours() + "h " + eventValue.getMinutes() + "m " + eventValue.getSeconds() + "s";

            value.setText(trackTime);
            value.setTextSize(18);
            value.setPadding(0, 0, 20, 0);

            Date updateDate = t.getBestTrackTime().getDate();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/d");

            dateUpdated.setText("on " + sdf.format(updateDate));
            dateUpdated.setTextSize(18);

            tr.addView(eventName);
            tr.addView(value);
            tr.addView(dateUpdated);

            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=10;
            int rightMargin=10;
            int bottomMargin = 2;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            tr.setLayoutParams(tableRowParams);

            timeTable.addView(tr);
        }

        ArrayList<String> eventNames = new ArrayList<>(eventsByName.keySet());
        ArrayAdapter<String> eventAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, eventNames);
        eventDropdown.setAdapter(eventAdapter);
        eventDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<TrackEvent> graphValues = eventsByName.get(eventDropdown.getSelectedItem().toString()).getTrackEvents();
                createEventGraph(graphValues);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createEventGraph(times.get(0).getTrackEvents());
    }

    public void setupGraphs(){
        GraphView ORMGraph = (GraphView) findViewById(R.id.ORMGraph);
        GraphView eventGraph = (GraphView) findViewById(R.id.eventGraph);

        ORMGraph.setTitle("Most Recent 1RMs");
        ORMGraph.getGridLabelRenderer().setHorizontalAxisTitle("Date Updated");
        ORMGraph.getGridLabelRenderer().setVerticalAxisTitle("Weight Lifted (lbs)");

        eventGraph.setTitle("Most Recent Event Times");
        eventGraph.getGridLabelRenderer().setHorizontalAxisTitle("Date Updated");
        eventGraph.getGridLabelRenderer().setVerticalAxisTitle("Event Time (s)");
    }

    public void createGraph(List<OneRepMax> orm1, List<OneRepMax> orm2){

        GraphView graph = (GraphView) findViewById(R.id.ORMGraph);
        graph.removeAllSeries();

        DataPoint[] dataPoints1 = new DataPoint[orm1.size()];
        for(int i = orm1.size()-1; i >= 0; i--){
            OneRepMax o = orm1.get(i);
            Log.i("datapoint", Integer.toString(o.getValue()));
            DataPoint d = new DataPoint(o.getDate(), o.getValue());
            dataPoints1[orm1.size()-1-i] = d;
        }

        DataPoint[] dataPoints2 = new DataPoint[orm2.size()];
        for(int i = orm2.size()-1; i >= 0; i--){
            OneRepMax o = orm2.get(i);
            Log.i("datapoint", Integer.toString(o.getValue()));
            DataPoint d = new DataPoint(o.getDate(), o.getValue());
            dataPoints2[orm2.size()-1-i] = d;
        }

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(dataPoints1);
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dataPoints2);
        series2.setColor(Color.RED);

        graph.addSeries(series1);
        graph.addSeries(series2);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ViewProfileActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

    }

    public void createEventGraph(List<TrackEvent> trackEvents){

        GraphView graph = (GraphView) findViewById(R.id.eventGraph);
        graph.removeAllSeries();

        DataPoint[] dataPoints = new DataPoint[trackEvents.size()];
        for(int i = trackEvents.size()-1; i >= 0; i--){
            TrackEvent t = trackEvents.get(i);

            float time = t.getTrackTime().getSeconds() + (t.getTrackTime().getMinutes()*60) + (t.getTrackTime().getHours()*3600);

            Log.i("Times: ", i + " " + Float.toString(time));

            DataPoint d = new DataPoint(t.getDate(), time);
            dataPoints[trackEvents.size()-1-i] = d;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        series.setColor(Color.GREEN);

        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ViewProfileActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
    }

    public void viewAthleteResults(View view){
        Intent intent = new Intent(ViewProfileActivity.this, AthleteWorkoutList.class);
        intent.putExtra("athleteID", profileUser.getId());

        startActivity(intent);
    }
}
