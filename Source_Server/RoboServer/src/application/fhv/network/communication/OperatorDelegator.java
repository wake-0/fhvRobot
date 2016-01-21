package network.communication;

import models.Client;
import network.IClientController;

public class OperatorDelegator extends Delegator {

	// Methods
	@Override
	protected void sendToClients(IClientController<Client> clientController, int flags, int command, byte[] payload) {
		for (Client c : clientController.getOperators()) {
			System.out.println("DelegateOperator message [" + new String(payload) + "]");
			targetCommunication.sendToClient(c, flags, command, payload);
		}
	}
}
