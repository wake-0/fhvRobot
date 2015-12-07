package controllers.factory;

import communication.configurations.IConfiguration;

public interface IClientFactory<T extends IConfiguration> {

	public T create();

}
