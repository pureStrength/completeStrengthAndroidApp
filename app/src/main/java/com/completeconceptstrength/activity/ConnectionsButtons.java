package com.completeconceptstrength.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.completeconceptstrength.R;

public class ConnectionsButtons extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections_buttons);

        setTitle("Connections");
    }


    public void openConnectedList(View view){
        Intent intent = new Intent(this, ConnectionsList.class);
        startActivity(intent);
    }

    public void openConnectionSearch(View view){
        Intent intent = new Intent(this, ConnectionSearch.class);
        startActivity(intent);
    }
}
