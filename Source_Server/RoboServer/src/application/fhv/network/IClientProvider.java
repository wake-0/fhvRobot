package network;

import java.util.List;

import communication.IClientConfiguration;
import models.Client;

public interface IClientProvider {

	public void addClient(Client client);
	public void removeClient(Client client);
	public List<IClientConfiguration> getClients();
}
