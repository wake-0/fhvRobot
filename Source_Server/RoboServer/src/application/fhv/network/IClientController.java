package network;

import java.util.List;

import communication.managers.IConfigurationManager;
import models.IExtendedConfiguration;

public interface IClientController<T extends IExtendedConfiguration> extends IConfigurationManager, IClientProvider<T> {

	void handleCommandReceived(T client, int command, byte[] payload);

	List<T> getOperators();
}
