package app.robo.fhv.roboapp.communication;

import java.util.ArrayList;
import java.util.List;

import communication.IConfiguration;
import communication.managers.IConfigurationManager;

/**
 * Created by Kevin on 24.11.2015.
 */
public class ConfigurationManager implements IConfigurationManager {

    private List<IConfiguration> configurations;

    public ConfigurationManager() {
        configurations = new ArrayList<>();
    }

    @Override
    public IConfiguration createConfiguration() {
        Configuration configuration = new Configuration();
        configurations.add(configuration);
        return configuration;
    }

    @Override
    public List<IConfiguration> getConfigurations() {
        return configurations;
    }
}
