package network.communication;

import controllers.ClientController;
import models.Client;

public class CommunicationDelegator {

	// Fields
	private Communication channelA;
	private final ClientController<Client> channelAClients;

	private Communication channelB;
	private final ClientController<Client> channelBClients;

	// Constructor
	public CommunicationDelegator(ClientController<Client> channelAClients, ClientController<Client> channelBClients) {
		this.channelAClients = channelAClients;
		this.channelBClients = channelBClients;
	}

	// Methods
	public void DelegateMessage(Communication channel, int command, byte[] payload) {
		// TODO: Add a condition controller, which checks if a specific
		// operation is allowed
		try {
			if (channel == getChannelA()) {
				sendToChannelClients(channelB, channelBClients, command, payload);
			} else if (channel == getChannelB()) {
				sendToChannelClients(channelA, channelAClients, command, payload);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendToChannelClients(Communication channel, ClientController<Client> clientController, int command,
			byte[] payload) {
		for (Client c : clientController.getClients()) {
			System.out.println("Send message [" + new String(payload) + "]");
			channel.sendToClient(c, command, payload);
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
