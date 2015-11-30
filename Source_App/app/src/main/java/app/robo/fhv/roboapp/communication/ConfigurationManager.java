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
    private Configuration configuration;

    public ConfigurationManager() {
        configuration = new Configuration();
        configurations = new ArrayList<>();
        configurations.add(configuration);
    }

    @Override
    public IConfiguration createConfiguration() {
        return configuration;
    }

    @Override
    public List<IConfiguration> getConfigurations() {
        return configurations;
    }
}
