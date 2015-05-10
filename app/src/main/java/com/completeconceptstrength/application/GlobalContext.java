package com.completeconceptstrength.application;

import android.app.Application;

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
     * Default constructor
     */
    public GlobalContext() {
        if(_serviceClient == null) {
            setServiceClient(new ServiceClient());
        }
    }

    /**
     * Get the service client
     * @return
     */
    public IServiceClientWrapper getServiceClient() {
        return _serviceClient;
    }

    /**
     * Set the service client
     * @param serviceClient
     */
    public void setServiceClient(final IServiceClientWrapper serviceClient) {
        _serviceClient = serviceClient;
    }
}
