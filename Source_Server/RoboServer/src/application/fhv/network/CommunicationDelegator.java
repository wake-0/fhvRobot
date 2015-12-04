package network;

import communication.commands.Commands;
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
				for (Client c : channelBClients.getClients()) {
					c.setSendData(new String(data));
					System.out.println("Send message [" + new String(data) + "]");
					getChannelB().sendToClient(c, Commands.DRIVE_BOTH, data);
				}
			} else if (channel == getChannelB()) {
				for (Client c : channelAClients.getClients()) {
					c.setSendData(new String(data));
					System.out.println("Send message [" + new String(data) + "]");
					getChannelA().sendToClient(c, Commands.DRIVE_BOTH, data);
				}
			}
		} catch (Exception e) {
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
