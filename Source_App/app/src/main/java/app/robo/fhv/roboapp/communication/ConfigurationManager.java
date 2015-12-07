package app.robo.fhv.roboapp.communication;

import java.util.ArrayList;
import java.util.List;

import communication.IConfiguration;
import communication.managers.IConfigurationManager;

/**
 * Created by Kevin on 24.11.2015.
 */
public class ConfigurationManager implements IConfigurationManager {

    // Fields
    private List<IConfiguration> configurations;
    private Configuration configuration;

    // Constructor
    public ConfigurationManager() {
        configuration = new Configuration();
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
