package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.ConnectionsAdapter;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.List;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.services.impl.UserConnectionClientService;


public class ConnectionSearch extends AppCompatActivity {

    GlobalContext globalContext;
    UserConnectionClientService connectionService;
    User user;
    public List<UserConnectionResponse> queryResults;
    public ListView searchList;
    public SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_search);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        connectionService = globalContext.getUserConnectionClientService();

        searchView = (SearchView) findViewById(R.id.searchView1);
        searchList = (ListView) findViewById(R.id.searchList);
        getSearchQuery();
    }

    public void getSearchQuery() {
        Log.i("getSearchQuery", "entered getSearchQuery function");

        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i("getSearchQuery", "onQueryTextSubmit");
                GetSearchResults getSearchResults = new GetSearchResults(user, query);
                getSearchResults.execute((Void) null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                GetSearchResults getSearchResults = new GetSearchResults(user, query);
                getSearchResults.execute((Void) null);

                Log.i("getSearchQuery", "onQueryTextChange");
                return true;
            }
        });
    }

    public class GetSearchResults extends AsyncTask<Void, Void, Boolean> {
        private User localUser;
        private String query;

        GetSearchResults(final User user, String query) {localUser = user; this.query = query; }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            Log.i("doInBackground", "User currently searching: " + localUser);

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
                //String query = searchView.getQuery().toString();
                queryResults = connectionService.searchConnectionsByUser(localUser.getId(), query);

                if(queryResults!= null) {
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
                    Log.e("doInBackground", "Error getting query results with status code: " + response.getStatusLine().getStatusCode());
                }
                else {
                    Log.e("doInBackground", "Get search results response is null");
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success && queryResults != null && !queryResults.isEmpty()){
                ConnectionsAdapter queryAdapter = new ConnectionsAdapter(ConnectionSearch.this,
                        R.layout.connection_entry_item, localUser, globalContext);

                searchList = (ListView) findViewById(R.id.searchList);
                searchList.setTextFilterEnabled(true);
                searchList.setAdapter(queryAdapter);

                for(final UserConnectionResponse u : queryResults) {
                    queryAdapter.add(u);
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful or no results found");
            }
        }
    }

}
