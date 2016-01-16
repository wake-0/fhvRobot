package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import models.Client;
import network.IClientController;

public class AppCommunication extends Communication {

	// Fields
	private final CommunicationDelegator delegator;

	// Constructor
	public AppCommunication(IClientController<Client> clientController, CommunicationDelegator delegator, int port)
			throws SocketException {
		super(clientController, port);
		this.delegator = delegator;
	}

	// Methods
	@Override
	protected boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client) {
		try {
			byte[] payload = pdu.getPayload();
			int command = pdu.getCommand();
			int flags = pdu.getFlags();

			// Only for test purposes
			client.setSendData(new String(payload));
			clientController.handleCommandReceived(client, command, payload);

			
			
			// This delegator is used to communicate with the robos
			if (delegator != null && client.getIsOperator()) {
				delegator.DelegateMessage(this, flags, command, payload);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}
