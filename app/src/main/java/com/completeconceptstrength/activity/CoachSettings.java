package com.completeconceptstrength.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.completeconceptstrength.R;
import com.completeconceptstrength.application.GlobalContext;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.UserClientService;

public class CoachSettings extends ActionBarActivity {

    GlobalContext globalContext;
    User user;
    UserClientService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_settings);

        globalContext = (GlobalContext)getApplicationContext();
        user = globalContext.getLoggedInUser();
        userService = globalContext.getUserClientService();

        setUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coach_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUserDetails(){
        EditText userFirstName = (EditText) findViewById(R.id.coachFirstName);
        userFirstName.setText(user.getFirstName());

        EditText userLastName = (EditText) findViewById(R.id.coachLastName);
        userLastName.setText(user.getLastName());

        EditText userOrg = (EditText) findViewById(R.id.coachOrg);
        userOrg.setText(getOrganization());

        EditText coachEmail = (EditText) findViewById(R.id.coachEmail);
        coachEmail.setText(user.getEmail());
    }

    public String getOrganization(){
        if(user.getOrganization() != null){
            return user.getOrganization();
        }
        else {
            return "N/A";
        }
    }

    public void editProfile(View view){
        EditText userFirstName = (EditText) findViewById(R.id.coachFirstName);
        userFirstName.setEnabled(true);

        EditText userLastName = (EditText) findViewById(R.id.coachLastName);
        userLastName.setEnabled(true);

        EditText userOrg = (EditText) findViewById(R.id.coachOrg);
        userOrg.setEnabled(true);

        EditText coachEmail = (EditText) findViewById(R.id.coachEmail);
        coachEmail.setEnabled(true);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(false);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(true);
    }

    public void saveProfile(View view){
        EditText userFirstName = (EditText) findViewById(R.id.coachFirstName);
        if(!user.getFirstName().equals(userFirstName.getText().toString())){
            user.setFirstName(userFirstName.getText().toString());
            Log.i("saveProfile", "New First Name: " + userFirstName.getText().toString());
        }
        userFirstName.setEnabled(false);

        EditText userLastName = (EditText) findViewById(R.id.coachLastName);
        if(!user.getLastName().equals(userLastName.getText().toString())){
            user.setLastName(userLastName.getText().toString());
            Log.i("saveProfile", "New Last Name: " + userLastName.getText().toString());
        }
        userLastName.setEnabled(false);

        EditText userOrg = (EditText) findViewById(R.id.coachOrg);
        if(!user.getOrganization().equals(userOrg.getText().toString())){
            user.setOrganization(userOrg.getText().toString());
            Log.i("saveProfile", "New Organization: " + userOrg.getText().toString());
        }
        userOrg.setEnabled(false);

        EditText coachEmail = (EditText) findViewById(R.id.coachEmail);
        if(!user.getEmail().equals(coachEmail.getText().toString())){
            user.setEmail(coachEmail.getText().toString());
            Log.i("saveProfile", "New Email: " + coachEmail.getText().toString());
        }
        coachEmail.setEnabled(false);

        Button editButton = (Button) findViewById(R.id.buttonEdit);
        editButton.setEnabled(true);

        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setEnabled(false);

        globalContext.setLoggedInUser(user);
        //returns a boolean
        if(!userService.update(user.getId(), user)){
            Log.e("saveProfile", "Unable to update user");
            HttpResponse serviceResponse = userService.getLastResponse();
            //Log.e("saveProfile", serviceResponse.getStatusLine().getStatusCode());
        }
    }

}
