package network;

import java.util.List;

import communication.IClientConfiguration;
import models.Client;

public interface IClientProvider {
	public void addRoboClient(Client client);
	public void removeRoboClient(Client client);
	public List<IClientConfiguration> getRoboClients();
	
	public void addAppClient(Client client);
	public void removeAppClient(Client client);
	public List<IClientConfiguration> getAppClients();
}
