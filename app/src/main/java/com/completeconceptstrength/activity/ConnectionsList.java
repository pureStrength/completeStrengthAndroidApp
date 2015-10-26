package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import completeconceptstrength.model.user.impl.User;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.services.impl.UserConnectionClientService;

public class ConnectionsList extends AppCompatActivity {

    GlobalContext globalContext;
    UserConnectionClientService connectionService;
    User user;
    public List<UserConnectionResponse> pendingConnections;
    public ArrayList<ConnectedUser> pendingConnect;
    public List<UserConnectionResponse> existingConnections;
    public ArrayList<ConnectedUser> existingConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections_list);

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

            Log.i("doInBackground", "User to get connections: " + localUser);

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
                pendingConnections = connectionService.getPendingConnections(localUser.getId());
                existingConnections = connectionService.getExistingConnections(localUser.getId(), null);

                if(pendingConnections!= null && existingConnections!=null) {
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
        protected void onPostExecute(final Boolean success){
            if(success){
                if(pendingConnections != null && !pendingConnections.isEmpty()){
                    pendingConnect = getConnUsers(pendingConnections);
                    ConnectionsAdapter pendAdapter = new ConnectionsAdapter(ConnectionsList.this,
                            R.layout.connection_entry_item);

                    ListView pendingList = (ListView) findViewById(R.id.pendingList);
                    pendingList.setAdapter(pendAdapter);

                    for(final ConnectedUser u : pendingConnect) {
                        pendAdapter.add(u);
                    }
                }

                if(existingConnections != null && !existingConnections.isEmpty()) {
                    existingConnect = getConnUsers(existingConnections);
                    ConnectionsAdapter existAdapter = new ConnectionsAdapter(ConnectionsList.this,
                            R.layout.connection_entry_item);

                    ListView existingList = (ListView) findViewById(R.id.existingList);
                    existingList.setAdapter(existAdapter);

                    for (final ConnectedUser u : existingConnect) {
                        existAdapter.add(u);
                    }
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
