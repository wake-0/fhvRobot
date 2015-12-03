package models;

import controllers.factory.IClientFactory;

public class ClientFactory implements IClientFactory<Client> {

	@Override
	public Client create() {
		return new Client();
	}

}
