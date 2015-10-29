package network;

import models.Client;

public interface IClientProvider {

	public void addClient(Client client);
	public void removeClient(Client client);
	
}
