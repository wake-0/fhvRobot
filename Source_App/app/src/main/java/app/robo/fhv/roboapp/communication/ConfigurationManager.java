package app.robo.fhv.roboapp.communication;

import android.content.res.*;

import java.util.ArrayList;
import java.util.List;

import communication.configurations.Configuration;
import communication.configurations.IConfiguration;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import communication.managers.IConfigurationManager;
import communication.configurations.*;

/**
 * Created by Kevin on 24.11.2015.
 */
public class ConfigurationManager implements IConfigurationManager {

    // Fields
    private List<IConfiguration> configurations;
    private Configuration configuration;

    // Constructor
    public ConfigurationManager() {

        int sessionId = ConfigurationSettings.DEFAULT_SESSION_ID;
        int port = GlobalSettings.SERVER_PORT;
        String address = GlobalSettings.SERVER_ADDRESS;
        //String address = GlobalSettings.LOCAL_ADDRESS;

        configuration = new Configuration(sessionId, port, address);
        configurations = new ArrayList<>();

        configurations.add(configuration);
    }

    // Methods
    @Override
    public IConfiguration createConfiguration() {
        return configuration;
    }

    @Override
    public List<IConfiguration> getConfigurations() {
        return configurations;
    }
}
