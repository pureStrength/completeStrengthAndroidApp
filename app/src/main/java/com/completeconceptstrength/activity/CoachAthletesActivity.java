package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.model.user.impl.UserType;
import completeconceptstrength.services.impl.UserConnectionClientService;

public class CoachAthletesActivity extends AppCompatActivity {

    GlobalContext globalContext;
    UserConnectionClientService connectionService;
    User user;
    public List<UserConnectionResponse> connections;
    public List<UserConnectionResponse> athleteConnections;
    public ArrayList<ConnectedUser> athleteConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_athletes);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        connectionService = globalContext.getUserConnectionClientService();
        final GetUserConnections getConnTask = new GetUserConnections(user);
        getConnTask.execute((Void) null);

    }

    public ArrayList<ConnectedUser> getConnUsers(List<UserConnectionResponse> connUsers){
        ArrayList<ConnectedUser> cUsers = new ArrayList<ConnectedUser>();

        if(connUsers == null || connUsers.isEmpty()){
            Log.e("getConnectedNames","No Connections pulled from server");
            return null;
        }

        for(UserConnectionResponse u: connUsers){
            String name = u.getUser().getFirstName() + " " + u.getUser().getLastName();
            cUsers.add(new ConnectedUser(name, u.getUser().getEmail(), u.getUser().getOrganization()));
        }

        return cUsers;
    }

    /**
     * Represents an asynchronous profile update task used to update
     * the user's details.
     */
    public class GetUserConnections extends AsyncTask<Void, Void, Boolean> {

        private User localUser;

        GetUserConnections(final User user) {
            localUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "Starting to get coach's athletes");

            // Set service class
            if(connectionService == null) {
                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }
                connectionService = globalContext.getUserConnectionClientService();
            }

            // Run the service
            if(connectionService != null) {
                athleteConnections = connectionService.getExistingConnections(localUser.getId(), UserType.ATHLETE);

                if(athleteConnections!=null) {
                    Log.i("doInBackground","No connected athletes");
                    result = true;
                }
            } else {
                Log.e("doInBackground", "userConnectionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {

                final HttpResponse response = connectionService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user connections with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get user connections response is null");
                }

            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success && athleteConnections != null && !athleteConnections.isEmpty()){
                athleteConnect = getConnUsers(athleteConnections);
                ConnectionsAdapter athleteAdapter = new ConnectionsAdapter(CoachAthletesActivity.this,
                        R.layout.connection_entry_item);

                ListView athleteList = (ListView) findViewById(R.id.athleteList);
                athleteList.setAdapter(athleteAdapter);

                for(final ConnectedUser u : athleteConnect) {
                    athleteAdapter.add(u);
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }
}
