package network.communication;

import models.Client;
import network.IClientController;

public abstract class Delegator {

	// Fields
	protected Communication targetCommunication;

	public void delegateMessage(int flags, int command, byte[] payload) {
		if (targetCommunication == null) {
			return;
		}

		try {
			sendToClients(targetCommunication.getClientController(), flags, command, payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void sendToClients(IClientController<Client> clientController, int flags, int command,
			byte[] payload);

	public Communication getTargetCommunication() {
		return targetCommunication;
	}

	public void setTargetCommunication(Communication channelA) {
		this.targetCommunication = channelA;
	}
}
