package app.robo.fhv.roboapp.communication;

import communication.IConfiguration;

/**
 * Created by Kevin on 24.11.2015.
 */
public class Configuration implements IConfiguration {

    // Fields
    private int sessionId;
    private String ipAddress;
    private int port;

    // Methods
    @Override
    public void setSessionId(int i) {
        sessionId = i;
    }

    @Override
    public int getSessionId() {
        return sessionId;
    }

    @Override
    public void setIpAddress(String s) {
        ipAddress = s;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void setPort(int i) {
        port = i;
    }

    @Override
    public int getPort() {
        return port;
    }
}
