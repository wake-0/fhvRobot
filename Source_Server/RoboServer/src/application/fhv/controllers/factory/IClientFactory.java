package controllers.factory;

import communication.IConfiguration;

public interface IClientFactory<T extends IConfiguration> {

	public T create();

}
