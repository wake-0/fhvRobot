package network.communication;

import models.Client;
import network.IClientController;

public class CommunicationDelegator {

	// Fields
	private Communication channelA;
	private final IClientController<Client> channelAClients;

	private Communication channelB;
	private final IClientController<Client> channelBClients;

	// Constructor
	public CommunicationDelegator(IClientController<Client> channelAClients,
			IClientController<Client> channelBClients) {
		this.channelAClients = channelAClients;
		this.channelBClients = channelBClients;
	}

	// Methods
	public void DelegateMessage(Communication channel, int flags, int command, byte[] payload) {
		// TODO: Add a condition controller, which checks if a specific
		// operation is allowed
		try {
			if (channel == getChannelA()) {
				sendToChannelClients(channelB, channelBClients, flags, command, payload);
			} else if (channel == getChannelB()) {
				sendToChannelClients(channelA, channelAClients, flags, command, payload);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendToChannelClients(Communication channel, IClientController<Client> clientController, int flags,
			int command, byte[] payload) {
		for (Client c : clientController.getClients()) {
			System.out.println("Send message [" + new String(payload) + "]");
			channel.sendToClient(c, flags, command, payload);
		}
	}

	public Communication getChannelA() {
		return channelA;
	}

	public void setChannelA(Communication channelA) {
		this.channelA = channelA;
	}

	public Communication getChannelB() {
		return channelB;
	}

	public void setChannelB(Communication channelB) {
		this.channelB = channelB;
	}
}
