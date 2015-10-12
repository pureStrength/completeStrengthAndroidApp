package com.completeconceptstrength.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.completeconceptstrength.R;

import java.util.ArrayList;

import completeconceptstrength.model.user.impl.UserConnectionResponse;

/**
 * Created by Jessica on 10/7/2015.
 */
public class ConnectionsTab1 extends Fragment {

    ListView pendView;
    ArrayList<UserConnectionResponse> userConnectionResponses;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.connections_tab_1,container,false);

        // Setup the list view
        final ListView connectionsListView = pendView;
        final ConnectionsAdapter connectionsAdapter = new ConnectionsAdapter(getActivity(), R.layout.connection_entry_item);
        connectionsListView.setAdapter(connectionsAdapter);

        // Populate the list, through the adapter
        /*for(int i = 0; i < userConnectionResponses.size(); i++) {
            connectionsAdapter.add(userConnectionResponses.get(i));
        }*/
        return v;
    }

    public void setPendView(ListView lv){
        this.pendView = lv;
    }

    public void setUserConnectionResponses(ArrayList<UserConnectionResponse> userResp){
        this.userConnectionResponses = userResp;
    }
}
