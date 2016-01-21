package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.commands.Commands;
import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import controllers.PersistencyController;
import models.Client;
import network.IClientController;

public class AppCommunication extends Communication {

	// Constructor
	public AppCommunication(IClientController<Client> clientController, Delegator delegator, int port,
			PersistencyController persistencyController) throws SocketException {
		super(clientController, delegator, port, persistencyController);
	}

	// Methods
	@Override
	protected boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client) {
		try {
			byte[] payload = pdu.getPayload();
			int command = pdu.getCommand();

			// Only for test purposes
			client.setSendData(new String(payload));
			clientController.handleCommandReceived(client, command, payload);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected boolean isCommandToDelegate(int command) {
		return command == Commands.DRIVE_BOTH ||
				command == Commands.DRIVE_LEFT ||
				command == Commands.DRIVE_RIGHT;
	}
}
