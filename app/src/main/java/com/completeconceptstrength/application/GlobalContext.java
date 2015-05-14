package com.completeconceptstrength.application;

import android.app.Application;

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
    private User _loggedInUser;
    /**
     * Store the logged in Athlete so they can be accessed in any activity
     */
    private Athlete _loggedInAthlete;
    /**
     * Store the logged in Coach so they can be accessed in any activity
     */
    private Coach _loggedInCoach;

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
    public User getLoggedInUser() {
        return _loggedInUser;
    }

    /**
     * Set the logged in user
     * @param user the logged in user
     */
    public void setLoggedInUser(final User user) {
        _loggedInUser = user;
    }

    /**
     * Get the logged in athlete
     * @return logged in athlete
     */
    public Athlete getLoggedInAthlete() {
        return _loggedInAthlete;
    }

    /**
     * Set the logged in athlete
     * @param athlete logged in athlete
     */
    public void setLoggedInAthlete(final Athlete athlete) {
        _loggedInAthlete = athlete;
    }

    /**
     * Get the logged in coach
     * @return logged in coach
     */
    public Coach getLoggedInCoach() {
        return _loggedInCoach;
    }

    /**
     * Set the logged in coach
     * @param coach logged in coach
     */
    public void setLoggedInCoach(final Coach coach) {
        _loggedInCoach = coach;
    }
}
