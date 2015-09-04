package com.completeconceptstrength.config;

/**
 * Created by Kyle on 9/2/2015.
 */
public enum ServerConfig {
    //=============================================================================================
    // CONSTANTS
    //=============================================================================================
    LOCAL_SERVER("10.0.2.2", "8080"),
    TEST_SERVER("107.203.223.129", "80");
    //=============================================================================================
    // VARIABLES
    //=============================================================================================
    private final String _ipAddress;
    private final String _port;
    //=============================================================================================
    // CONSTRUCTORS
    //=============================================================================================
    /**
     * Enum constructor
     * @param type
     */
    ServerConfig(final String ipAddress, final String port) {
        _ipAddress = ipAddress;
        _port = port;
    }
    //=============================================================================================
    // PUBLIC METHODS
    //=============================================================================================

    /**
     * Get the ip address of the server configuration
     * @return
     */
    public String getIpAddress() {
        return _ipAddress;
    }

    /**
     * Get the port of the server configuration
     * @return
     */
    public String getPort() {
        return _port;
    }
}
