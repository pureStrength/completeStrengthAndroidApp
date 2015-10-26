package com.completeconceptstrength.activity;

import completeconceptstrength.model.user.impl.UserConnectionResponse;
import completeconceptstrength.model.user.impl.UserConnectionStatus;

/**
 * Created by Jessica on 10/12/2015.
 */
public class ConnectedUser {

    private String name;
    private String email;
    private String organization;
    private UserConnectionStatus connectionStatus;

    public ConnectedUser(String name, String email, String organization, UserConnectionStatus connectionStatus){
        this.name = name;
        this.email = email;
        this.organization = organization;
        this.connectionStatus = connectionStatus;
    }

    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getOrganization(){return organization;}
    public UserConnectionStatus getConnectionStatus(){return connectionStatus;}
}
