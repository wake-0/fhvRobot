package network;

import communication.managers.IConfigurationManager;
import controllers.IOperatorManager;
import models.IExtendedConfiguration;

public interface IClientController<T extends IExtendedConfiguration>
		extends IOperatorManager<T>, IConfigurationManager, IClientProvider<T> {

	void handleCommandReceived(T client, int command, byte[] payload);
}
