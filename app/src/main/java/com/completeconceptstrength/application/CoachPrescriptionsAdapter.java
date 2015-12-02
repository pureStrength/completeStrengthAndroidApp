package com.completeconceptstrength.application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.completeconceptstrength.R;
import com.completeconceptstrength.activity.PrescriptionViewActivity;

import completeconceptstrength.model.exercise.impl.PrescriptionDefinition;

/**
 * Created by Jessica on 10/27/2015.
 */
public class CoachPrescriptionsAdapter extends ArrayAdapter<PrescriptionDefinition> {

    private final int layout_resource;

    public CoachPrescriptionsAdapter(Context context, final int layout_resource){
        super(context, 0);
        this.layout_resource = layout_resource;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final PrescriptionDefinition prescriptionDefinition = getItem(position);

        viewHolder.rxName.setText(prescriptionDefinition.getName());

        viewHolder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PrescriptionViewActivity.class);
                intent.putExtra("prescriptionID", prescriptionDefinition.getId());

                getContext().startActivity(intent);
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

            viewHolder.rxName = (TextView) workingView.findViewById(R.id.rxName);
            viewHolder.viewButton = (Button) workingView.findViewById(R.id.viewRxButton);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }

    private static class ViewHolder {
        public TextView rxName;
        public Button viewButton;

    }


}
