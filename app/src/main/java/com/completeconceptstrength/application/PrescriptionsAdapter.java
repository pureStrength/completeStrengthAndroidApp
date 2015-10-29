package com.completeconceptstrength.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.completeconceptstrength.R;

import completeconceptstrength.model.exercise.impl.PrescriptionInstance;
import completeconceptstrength.serialization.DateSerializer;

/**
 * Created by Jessica on 10/26/2015.
 */
public class PrescriptionsAdapter extends ArrayAdapter<PrescriptionInstance> {

    private final int layout_resource;

    public PrescriptionsAdapter(Context context, int resource) {
        super(context, 0);
        layout_resource = resource;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final PrescriptionInstance prescription = getItem(position);

        viewHolder.prescriptionDate.setText(DateSerializer.formatDateWithDay(prescription.getDateAssigned()));

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

            viewHolder.prescriptionDate = (TextView) workingView.findViewById(R.id.prescriptionDate);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }

    private static class ViewHolder {
        public TextView prescriptionDate;
    }

}
