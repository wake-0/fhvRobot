package network;

import java.util.List;

import communication.managers.IConfigurationManager;
import controllers.ClientController.IOperatorChangedListener;
import models.IExtendedConfiguration;

public interface IClientController<T extends IExtendedConfiguration>
		extends IConfigurationManager<T>, IClientProvider<T> {

	void handleCommandReceived(T client, int command, byte[] payload);

	void addOperatorChangedListener(IOperatorChangedListener<T> operatorListener);

	void setOperator(T operator);

	void releaseOperator(T operator);

	List<T> getOperators();
}
