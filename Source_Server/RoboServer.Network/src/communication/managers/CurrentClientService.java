package communication.managers;

import com.google.inject.Singleton;

import communication.IClient;

@Singleton
public class CurrentClientService {
	
	private IClient client;

	public IClient getClient() {
		return client;
	}

	public void setClient(IClient client) {
		this.client = client;
	}
	
}
