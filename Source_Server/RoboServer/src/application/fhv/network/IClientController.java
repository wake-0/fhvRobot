package network;

import communication.configurations.IConfiguration;
import communication.managers.IConfigurationManager;

public interface IClientController<T extends IConfiguration> extends IConfigurationManager, IClientProvider<T> {

	void handleCommandReceived(T client, int command, byte[] payload);
}
