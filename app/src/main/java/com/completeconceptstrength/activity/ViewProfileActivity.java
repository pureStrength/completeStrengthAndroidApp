package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;
import org.w3c.dom.Text;

import completeconceptstrength.model.user.impl.Athlete;
import completeconceptstrength.model.user.impl.AthleteProfile;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.AthleteClientService;
import completeconceptstrength.services.impl.UserClientService;

public class ViewProfileActivity extends AppCompatActivity {

    GlobalContext globalContext;
    User user;
    UserClientService userClientService;
    User profileUser;
    AthleteClientService athleteClientService;
    AthleteProfile profileAthlete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        athleteClientService = globalContext.getAthleteClientService();
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
                final HttpResponse response = athleteClientService.getLastResponse();
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

        nameTV.setText(profileUser.getFirstName() + " " + profileUser.getLastName());
        emailTV.setText(profileUser.getEmail());
        orgTV.setText(profileUser.getOrganization());
    }
}
