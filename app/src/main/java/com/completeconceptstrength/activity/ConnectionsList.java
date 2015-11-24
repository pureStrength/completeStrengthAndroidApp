package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.ConnectionsAdapter;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.user.impl.User;

import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.services.impl.UserConnectionClientService;

public class ConnectionsList extends AppCompatActivity {

    GlobalContext globalContext;
    UserConnectionClientService connectionService;
    User user;

    public ConnectionsAdapter pendAdapter;
    public List<UserConnectionResponse> pendingConnections;

    ConnectionsAdapter existAdapter;
    public List<UserConnectionResponse> existingConnections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections_list);

        setTitle("Connections");

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        connectionService = globalContext.getUserConnectionClientService();

        pendAdapter = new ConnectionsAdapter(ConnectionsList.this,
                R.layout.connection_entry_item, user, globalContext);
        existAdapter = new ConnectionsAdapter(ConnectionsList.this,
                R.layout.connection_entry_item, user, globalContext);

        final GetUserConnections getConnTask = new GetUserConnections(user);
        getConnTask.execute((Void) null);
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
                    ListView pendingList = (ListView) findViewById(R.id.pendingList);
                    pendingList.setAdapter(pendAdapter);

                    for(final UserConnectionResponse u : pendingConnections) {
                        pendAdapter.add(u);
                    }
                }

                if(existingConnections != null && !existingConnections.isEmpty()) {
                    ListView existingList = (ListView) findViewById(R.id.existingList);
                    existingList.setAdapter(existAdapter);

                    for (final UserConnectionResponse u : existingConnections) {
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
