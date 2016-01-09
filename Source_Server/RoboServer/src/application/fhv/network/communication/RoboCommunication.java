package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import models.Client;
import network.IClientController;

public class RoboCommunication extends Communication {

	// Constructor
	public RoboCommunication(IClientController<Client> clientController, int port) throws SocketException {
		super(clientController, port);
	}

	// Methods
	@Override
	public boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client) {
		try {
			byte[] payload = pdu.getPayload();

			// Only for test purposes
			// client.setSendData(new String(payload));
			// sendToClient(client);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}
