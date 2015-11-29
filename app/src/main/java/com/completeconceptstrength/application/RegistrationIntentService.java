package com.completeconceptstrength.application;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.completeconceptstrength.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.UserClientService;

public class RegistrationIntentService extends IntentService {

    GlobalContext globalContext;
    User user;
    UserClientService userService;

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(token);

        } catch (Exception e) {
            Log.d("RegistrationIntentServ", "Failed to complete token refresh", e);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        globalContext = (GlobalContext)getApplicationContext();

        user = globalContext.getLoggedInUser();
        user.setEnableAndroidNotifications(true);
        user.setAndroidToken(token);

        final UpdateProfileTask updateTask = new UpdateProfileTask(user);
        updateTask.execute((Void) null);
    }

    public class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {

        private User localUser;
        private String alertTitle;
        private String alertMessage;
        private int alertIconNumber;

        UpdateProfileTask(final User user) {
            localUser = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;

            Log.i("doInBackground", "User to update: " + localUser);

            // Set service class
            if(userService == null) {

                // Get the global context
                if(globalContext == null) {
                    globalContext = (GlobalContext)getApplicationContext();
                }

                userService = globalContext.getUserClientService();
            }


            // Run the service
            if(userService != null) {
                result = userService.update(localUser.getId(), localUser);
                Log.i("Update User Profile", localUser.getAndroidToken() + " " + localUser.getEnableAndroidNotifications());
            } else {
                Log.e("doInBackground", "userService is null");
            }

            Log.d("doInBackground", "result: " + result);

            // Check the result of the service call and set the variables accordingly
            if(result == false) {
                alertTitle = "Unable to update settings";
                alertIconNumber = android.R.drawable.ic_dialog_alert;

                final HttpResponse response = userService.getLastResponse();
                if (response != null) {
                    Log.e("doInBackground", "Error updating user with status code: " + response.getStatusLine().getStatusCode());
                    alertMessage = "Error updating, \nPlease try again later.\n";
                }
                else {
                    Log.e("doInBackground", "Update user response is null");
                    alertMessage = "Unable to access server";
                }

            }

            return result;
        }
    }

}