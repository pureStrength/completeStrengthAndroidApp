package com.completeconceptstrength.application;

import android.app.Application;

import completeconceptstrength.model.user.IUser;
import completeconceptstrength.model.user.impl.Athlete;
import completeconceptstrength.model.user.impl.Coach;
import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.utils.IServiceClientWrapper;
import completeconceptstrength.services.utils.ServiceClient;

/**
 * This class is used to store global objects across activities
 * Created by Kyle on 5/10/2015.
 */
public class GlobalContext extends Application {

    /**
     * The service client is used to provide functionality for the client services
     */
    private IServiceClientWrapper _serviceClient;

    /**
     * Store the logged in user so they can be accessed in any activity
     */
    private IUser _loggedInUser;

    /**
     * Default constructor
     */
    public GlobalContext() {

    }

    /**
     * Get the service client
     * @return ServiceClient
     */
    public IServiceClientWrapper getServiceClient() {
        if(_serviceClient == null) {
            setServiceClient(new ServiceClient());
        }
        return _serviceClient;
    }

    /**
     * Set the service client
     * @param serviceClient the ServiceClient to set
     */
    public void setServiceClient(final IServiceClientWrapper serviceClient) {
        _serviceClient = serviceClient;
    }

    /**
     * Get the logged in user
     * @return logged in user
     */
    public IUser getLoggedInUser() {
        return _loggedInUser;
    }

    /**
     * Set the logged in user
     * @param user the logged in user
     */
    public void setLoggedInUser(final IUser user) {
        _loggedInUser = user;
    }
}
