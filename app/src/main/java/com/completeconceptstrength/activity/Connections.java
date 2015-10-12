package com.completeconceptstrength.activity;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Set;

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
    public Set<UserConnectionResponse> pendingConnections;
    public ArrayList<String> pendingConnect;
    public Set<UserConnectionResponse> existingConnections;
    public ArrayList<String> existingConnect;

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

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    public ArrayList<String> getConnectedNames(Set<UserConnectionResponse> connUsers){
        ArrayList<String> namesList = new ArrayList<String>();

        if(connUsers.isEmpty()){
            Log.e("getConnectedNames","No Connections pulled from server");
            return null;
        }

        for (UserConnectionResponse u: connUsers) {
            namesList.add(u.getUser().getFirstName() + " " + u.getUser().getLastName());
        }

        return namesList;
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

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v =inflater.inflate(R.layout.connections_tab_1,container,false);
            return v;
        }
    }

    /*// TODO Example ListFragment
    public class MessagesFragment extends ListFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.connections_tab_1, container,
                    false);

            String[] values = new String[] { "Message1", "Message2", "Message3" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            return rootView;
        }
    }*/

    public static class AthleteConnectionsTab2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.connections_tab_2,container,false);
            return v;
        }
    }

    /**
     * Represents an asynchronous profile update task used to update
     * the user's details.
     */
    public class GetUserConnections extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private String alertTitle;
        private String alertMessage;
        private int alertIconNumber;

        GetUserConnections(final User user) {
            localUser = user;
        }

        @Override
        protected void onPreExecute() {
            // Start the progress wheel spinner
            //progressRegister.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to update: " + localUser);

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
                existingConnections = connectionService.getExistingConnections(localUser.getId());

                if(pendingConnections!= null && existingConnections!=null) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "userConnectionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                alertTitle = "Unable to find connections";
                alertIconNumber = android.R.drawable.ic_dialog_alert;

                final HttpResponse response = connectionService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting user connections with status code: " + response.getStatusLine().getStatusCode());
                    alertMessage = "Error updating, \nPlease try again later.\n";
                }
                else {
                    Log.e("doInBackground", "Get user connections response is null");
                    alertMessage = "Unable to access server";
                }

            } else {
                alertIconNumber = android.R.drawable.ic_dialog_info;
                alertTitle = "Connections pulled";
                alertMessage = "Connections have been found";
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success){
                pendingConnect = getConnectedNames(pendingConnections);
                ArrayAdapter<Object> pendAdapter = new ArrayAdapter<>(Connections.this,
                        android.R.layout.simple_list_item_1, pendingConnect.toArray());

                ListView pendingList = (ListView) findViewById(R.id.pendingList);
                pendingList.setAdapter(pendAdapter);

                existingConnect = getConnectedNames(existingConnections);
                ArrayAdapter<Object> existAdapter = new ArrayAdapter<>(Connections.this,
                        android.R.layout.simple_list_item_1, existingConnect.toArray());

                ListView existingList = (ListView) findViewById(R.id.existingList);
                existingList.setAdapter(existAdapter);
            }
            else {
                Log.e("onPostExecute", "Execute unsuccessful");
            }
        }
    }

}
