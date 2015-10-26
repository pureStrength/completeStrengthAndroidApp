package com.completeconceptstrength.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.model.user.impl.UserConnectionStatus;

public final class ConnectionsAdapter extends ArrayAdapter<UserConnectionResponse> implements Filterable {

    private final int layout_resource;

    public ConnectionsAdapter(final Context context, final int layout_resource) {
        super(context, 0);
        this.layout_resource = layout_resource;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final UserConnectionResponse connUser = getItem(position);

        viewHolder.nameView.setText(connUser.getUser().getFirstName() + " " + connUser.getUser().getLastName());
        viewHolder.orgView.setText(connUser.getUser().getOrganization());

        UserConnectionStatus connectionType = connUser.getUserConnectionStatus();
        if(connectionType == UserConnectionStatus.CONNECTION_INTACT){
            viewHolder.actionButton.setText("Remove");
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove connected user from current user's connection list and connected user's list
                }
            });
        }
        else if(connectionType == UserConnectionStatus.CONNECTION_REQUEST_SENT){
            viewHolder.actionButton.setText("Cancel");
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove connection request from current user's connection list and requested user's list
                }
            });
        }
        else if(connectionType == UserConnectionStatus.CONNECTION_REQUEST_AVAILABLE){
            viewHolder.actionButton.setText("Accept");
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Accept user and add to current user's connection list and accepted user's connection list
                }
            });
        }
        else {
            viewHolder.actionButton.setText("Send");
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Accept user and add to current user's connection list and accepted user's connection list
                }
            });
        }

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

}
