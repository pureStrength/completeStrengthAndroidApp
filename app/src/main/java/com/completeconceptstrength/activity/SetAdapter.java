package com.completeconceptstrength.activity;

import android.content.Context;
import android.widget.ArrayAdapter;

import completeconceptstrength.model.exercise.impl.MainLiftSet;

/**
 * Created by Jessica on 10/26/2015.
 */
public class SetAdapter extends ArrayAdapter<MainLiftSet> {

    private final int layout_resource;

    public SetAdapter(Context context, final int layout_resource){
        super(context, 0);
        this.layout_resource = layout_resource;
    }
}
