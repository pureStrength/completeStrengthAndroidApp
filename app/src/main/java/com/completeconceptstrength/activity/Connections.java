package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.services.impl.UserConnectionClientService;

public class Connections extends AppCompatActivity {

    GlobalContext globalContext;
    UserConnectionClientService connectionService;
    User user;
    ViewPager pager;
    AthleteConnectionsViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Connections","Find"};
    int Numboftabs =2;
    public List<UserConnectionResponse> pendingConnections;
    public ArrayList<ConnectedUser> pendingConnect;
    public List<UserConnectionResponse> existingConnections;
    public ArrayList<ConnectedUser> existingConnect;
    public List<UserConnectionResponse> queryResults;
    public ArrayList<ConnectedUser> queryUserResults;
    public ListView searchList;
    public SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        connectionService = globalContext.getUserConnectionClientService();
        final GetUserConnections getConnTask = new GetUserConnections(user);
        getConnTask.execute((Void) null);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new AthleteConnectionsViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        View inflatedView = getLayoutInflater().inflate(R.layout.connections_tab_2, null);
        searchView = (SearchView) inflatedView.findViewById(R.id.searchView1);
        searchView.setQueryHint("Search Here");
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
                //GetSearchResults getSearchResults = new GetSearchResults(user, query);
                //getSearchResults.execute((Void) null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //GetSearchResults getSearchResults = new GetSearchResults(user, query);
                //getSearchResults.execute((Void) null);

                Log.i("getSearchQuery", "onQueryTextChange");
                return false;
            }
        });
    }

    public ArrayList<ConnectedUser> getConnUsers(List<UserConnectionResponse> connUsers){
        ArrayList<ConnectedUser> cUsers = new ArrayList<ConnectedUser>();

        if(connUsers.isEmpty()){
            Log.e("getConnectedNames","No Connections pulled from server");
            return null;
        }

        for(UserConnectionResponse u: connUsers){
            String name = u.getUser().getFirstName() + " " + u.getUser().getLastName();
            cUsers.add(new ConnectedUser(name, u.getUser().getEmail(), u.getUser().getOrganization()));
        }

        return cUsers;
    }


    static class AthleteConnectionsViewPagerAdapter extends FragmentStatePagerAdapter
    {
        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public AthleteConnectionsViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);
            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            if(position == 0) // if the position is 0 we are returning the First tab
            {
                AthleteConnectionsTab1 connections = new AthleteConnectionsTab1();
                return connections;
            }
            else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                AthleteConnectionsTab2 findConnections = new AthleteConnectionsTab2();
                return findConnections;
            }
        }

        // This method return the titles for the Tabs in the Tab Strip
        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip
        @Override
        public int getCount() {
            return NumbOfTabs;
        }
    }

    public static class AthleteConnectionsTab1 extends Fragment {

        public View v;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.connections_tab_1,container,false);
            this.v = v;
            return v;
        }

    }

    public static class AthleteConnectionsTab2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.connections_tab_2,container,false);
            return v;
        }
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
            if(success){
                queryUserResults = getConnUsers(queryResults);
                ConnectionsAdapter pendAdapter = new ConnectionsAdapter(Connections.this,
                        R.layout.connection_entry_item);

                searchList = (ListView) findViewById(R.id.searchList);
                searchList.setTextFilterEnabled(true);
                searchList.setAdapter(pendAdapter);

                for(final ConnectedUser u : queryUserResults) {
                    pendAdapter.add(u);
                }

            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
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
                pendingConnect = getConnUsers(pendingConnections);
                ConnectionsAdapter pendAdapter = new ConnectionsAdapter(Connections.this,
                        R.layout.connection_entry_item);

                ListView pendingList = (ListView) findViewById(R.id.pendingList);
                pendingList.setAdapter(pendAdapter);

                for(final ConnectedUser u : pendingConnect) {
                    pendAdapter.add(u);
                }

                existingConnect = getConnUsers(existingConnections);
                ConnectionsAdapter existAdapter = new ConnectionsAdapter(Connections.this,
                        R.layout.connection_entry_item);

                ListView existingList = (ListView) findViewById(R.id.existingList);
                existingList.setAdapter(existAdapter);

                for(final ConnectedUser u : existingConnect){
                    existAdapter.add(u);
                }
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
