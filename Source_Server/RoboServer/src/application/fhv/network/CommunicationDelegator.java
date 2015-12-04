package network;

import java.io.IOException;

import controllers.ClientController;
import models.Client;

public class CommunicationDelegator {

	private Communication channelA;
	private final ClientController<Client> channelAClients;

	private Communication channelB;
	private final ClientController<Client> channelBClients;

	public CommunicationDelegator(ClientController<Client> channelAClients, ClientController<Client> channelBClients) {
		this.channelAClients = channelAClients;
		this.channelBClients = channelBClients;
	}

	public void DelegateMessage(Communication channel, byte[] data) {
		try {
			if (channel == getChannelA()) {
				for (Client c : channelAClients.getClients()) {
					c.setSendData(new String(data));
					getChannelB().sendToClient(c);
				}
			} else if (channel == getChannelB()) {
				for (Client c : channelBClients.getClients()) {
					c.setSendData(new String(data));
					getChannelA().sendToClient(c);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
