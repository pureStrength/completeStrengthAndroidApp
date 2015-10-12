package com.completeconceptstrength.activity;

/**
 * Created by Jessica on 10/12/2015.
 */
public class ConnectedUser {

    private String name;
    private String email;
    private String organization;

    public ConnectedUser(String name, String email, String organization){
        this.name = name;
        this.email = email;
        this.organization = organization;
    }

    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getOrganization(){return organization;}
}
