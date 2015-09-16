package com.completeconceptstrength.application;

import android.app.Application;

import com.completeconceptstrength.config.ServerConfig;

import completeconceptstrength.model.user.impl.User;
import completeconceptstrength.services.impl.UserClientService;
import completeconceptstrength.services.utils.IServiceClient;
import completeconceptstrength.services.utils.ServiceClient;

/**
 * This class is used to store global objects across activities
 * Created by Kyle on 5/10/2015.
 */
public class GlobalContext extends Application {


    private ServerConfig _serverConfig = ServerConfig.TEST_SERVER;

    /**
     * Store the logged in user so they can be accessed in any activity
     */
    private User _loggedInUser;

    /**
     * The service client is used to provide functionality for the client services
     */
    private IServiceClient _serviceClient;

    /**
     * Default constructor
     */
    public GlobalContext() {

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
     * Get the service client
     * @return ServiceClient
     */
    public IServiceClient getServiceClient() {
        if(_serviceClient == null) {
            setServiceClient(new ServiceClient());
        }
        return _serviceClient;
    }

    /**
     * Set the service client
     * @param serviceClient the ServiceClient to set
     */
    public void setServiceClient(final IServiceClient serviceClient) {
        _serviceClient = serviceClient;
    }

    /**
     * Get the user client service
     * @return
     */
    public UserClientService getUserClientService() {
        return new UserClientService(getServiceClient(), _serverConfig.getIpAddress(), _serverConfig.getPort());
    }
}
