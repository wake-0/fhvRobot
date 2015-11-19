package communication.managers;

import java.util.List;

import communication.IClientConfiguration;

public interface IClientManager {

	public IClientConfiguration createClientConfiguration();
	public List<IClientConfiguration> getConfigurations();
}
