package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import controllers.ClientController;
import models.Client;

public class AppCommunication extends Communication {

	private final CommunicationDelegator delegator;

	public AppCommunication(ClientController<Client> clientController, CommunicationDelegator delegator, int port)
			throws SocketException {
		super(clientController, port);
		this.delegator = delegator;
	}

	@Override
	protected boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client) {
		try {
			byte[] payload = pdu.getPayload();
			int command = pdu.getCommand();

			// Only for test purposes
			client.setSendData(new String(payload));
			sendToClient(client);

			if (delegator != null) {
				delegator.DelegateMessage(this, command, payload);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}
