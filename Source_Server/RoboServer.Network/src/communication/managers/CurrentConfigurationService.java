package communication.managers;

import com.google.inject.Singleton;

import communication.IClientConfiguration;

@Singleton
public class CurrentConfigurationService {
	
	private IClientConfiguration client;

	public IClientConfiguration getClient() {
		return client;
	}

	public void setClient(IClientConfiguration client) {
		this.client = client;
	}
	
}
