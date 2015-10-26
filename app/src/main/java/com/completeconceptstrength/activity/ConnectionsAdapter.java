package com.completeconceptstrength.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.model.user.impl.UserConnectionStatus;
import completeconceptstrength.services.impl.UserConnectionClientService;

public final class ConnectionsAdapter extends ArrayAdapter<UserConnectionResponse> implements Filterable {

    private final int layout_resource;
    GlobalContext globalContext;
    UserConnectionClientService connectionService;
    User user;

    public ConnectionsAdapter(final Context context, final int layout_resource, User u, GlobalContext gc) {
        super(context, 0);
        this.layout_resource = layout_resource;

        globalContext = gc;
        user = globalContext.getLoggedInUser();
        connectionService = globalContext.getUserConnectionClientService();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final UserConnectionResponse connUser = getItem(position);

        viewHolder.nameView.setText(connUser.getUser().getFirstName() + " " + connUser.getUser().getLastName());
        viewHolder.orgView.setText(connUser.getUser().getOrganization());

        final UserConnectionStatus connectionType = connUser.getUserConnectionStatus();
        if(connectionType == UserConnectionStatus.CONNECTION_INTACT){
            viewHolder.actionButton.setText("Remove");
        }
        else if(connectionType == UserConnectionStatus.CONNECTION_REQUEST_SENT){
            viewHolder.actionButton.setText("Accept");
        }
        else if(connectionType == UserConnectionStatus.CONNECTION_REQUEST_AVAILABLE){
            viewHolder.actionButton.setText("Cancel");
        }
        else {
            viewHolder.actionButton.setText("Send");
        }

        viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Accept user and add to current user's connection list and accepted user's connection list
                ButtonActions getButtonActions = new ButtonActions(user, connUser, connectionType);
                getButtonActions.execute((Void) null);
            }
        });

        return view;
    }

    private View getWorkingView(final View convertView) {
        // The workingView is basically just the convertView re-used if possible
        // or inflated new if not possible
        View workingView = null;

        if(null == convertView) {
            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            workingView = inflater.inflate(layout_resource, null);
        } else {
            workingView = convertView;
        }

        return workingView;
    }

    private ViewHolder getViewHolder(final View workingView) {
        // The viewHolder allows us to avoid re-looking up view references
        // Since views are recycled, these references will never change
        final Object tag = workingView.getTag();
        ViewHolder viewHolder = new ViewHolder();


        if(null == tag || !(tag instanceof ViewHolder)) {

            viewHolder.nameView = (TextView) workingView.findViewById(R.id.connName);
            viewHolder.orgView = (TextView) workingView.findViewById(R.id.connOrganization);
            viewHolder.actionButton = (Button) workingView.findViewById(R.id.actionButton);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }

    private static class ViewHolder {
        public TextView nameView;
        public TextView orgView;
        public Button actionButton;

    }

    public class ButtonActions extends AsyncTask<Void, Void, Boolean> {
        private User localUser;
        private UserConnectionResponse connUser;
        private UserConnectionStatus status;

        ButtonActions(final User user, UserConnectionResponse connUser, UserConnectionStatus status) {
            localUser = user;
            this.connUser = connUser;
            this.status = status;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            Log.i("doInBackground", "User currently searching: " + localUser);

            // Set service class
            if (connectionService == null) {
                // Get the global context
                if (globalContext == null) {
                    return result;
                }
                connectionService = globalContext.getUserConnectionClientService();
            }

            // Run the service
            if (connectionService != null) {
                boolean success;
                if(status == UserConnectionStatus.CONNECTION_INTACT){
                    success = connectionService.disconnectUsers(localUser.getId(), connUser.getUser().getId());
                }
                else if(status == UserConnectionStatus.CONNECTION_REQUEST_AVAILABLE){
                    success = connectionService.denyUserConnection(localUser.getId(), connUser.getUser().getId());
                }
                else if(status == UserConnectionStatus.CONNECTION_REQUEST_SENT){
                    success = connectionService.acceptUserConnection(localUser.getId(), connUser.getUser().getId());
                }
                else{
                    success = connectionService.requestUserConnection(localUser.getId(), connUser.getUser().getId());
                }

                if (success) {
                    result = true;
                }
            } else {
                Log.e("doInBackground", "userConnectionService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if (result == false) {
                final HttpResponse response = connectionService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error getting query results with status code: " + response.getStatusLine().getStatusCode());
                } else {
                    Log.e("doInBackground", "Get search results response is null");
                }
            }

            return result;
        }
    }
}
