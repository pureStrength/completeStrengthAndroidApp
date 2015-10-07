package com.completeconceptstrength.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.completeconceptstrength.R;

import java.text.DateFormat;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.model.user.impl.UserConnectionResponse;

/**
 * Created by Jessica on 10/6/2015.
 */
public final class ConnectionsAdapter extends ArrayAdapter<UserConnectionResponse> {

    private final int resource;

    public ConnectionsAdapter(final Context context, final int resource) {
        super(context, 0);
        this.resource = resource;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        final View view = convertView;
        final ViewHolder viewHolder = getViewHolder(view);
        final UserConnectionResponse userConnectionResponse = getItem(position);
        final User user = userConnectionResponse.getUser();

        viewHolder.nameView.setText(user.getFirstName() + " " + user.getLastName());

        viewHolder.orgView.setText(user.getOrganization());

        // Setting image view is also simple
        //viewHolder.profPicView.setImageResource();

        return view;
    }

    private ViewHolder getViewHolder(final View workingView) {
        // The viewHolder allows us to avoid re-looking up view references
        // Since views are recycled, these references will never change
        final Object tag = workingView.getTag();
        ViewHolder viewHolder = new ViewHolder();


        if(null == tag || !(tag instanceof RecyclerView.ViewHolder)) {


            viewHolder.nameView = (TextView) workingView.findViewById(R.id.connName);
            viewHolder.orgView = (TextView) workingView.findViewById(R.id.connOrganization);
            viewHolder.profPicView = (ImageView) workingView.findViewById(R.id.connProfPic);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }

    private static class ViewHolder {
        public TextView nameView;
        public TextView orgView;
        public ImageView profPicView;
    }

}
