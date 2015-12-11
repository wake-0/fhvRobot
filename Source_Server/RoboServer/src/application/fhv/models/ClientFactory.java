package models;

import controllers.factory.IClientFactory;

public class ClientFactory implements IClientFactory<Client> {

	// Methods
	@Override
	public Client create() {
		return new Client();
	}

}
