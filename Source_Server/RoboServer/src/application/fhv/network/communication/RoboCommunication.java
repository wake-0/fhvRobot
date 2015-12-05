package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.commands.Commands;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import models.Client;

public class RoboCommunication extends Communication {

	public RoboCommunication(CommunicationManager manager, int port) throws SocketException {
		super(manager, port);
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender) {
		try {
			// TODO: check length and real payload are equal

			Client client = (Client) manager.getCurrentConfiguration();
			byte[] payload = pdu.getPayload();
			int command = pdu.getCommand();

			// This means register name
			if (pdu.getCommand() == Commands.CHANGE_NAME) {
				String name = new String(payload);
				client.setName(name);

				// Create answer pdu
				// byte[] nameBytes = Arrays.copyOfRange(payload, 0,
				// pdu.getPayloadLength());
				DatagramPacket datagram = manager.createDatagramPacket(client, 1, new byte[] { 1 });
				sender.answer(client, datagram);
			} else {
				// Only for test purposes
				client.setSendData(new String(payload));
				sendToClient(client);
			}

			// TODO: handle other message
			client.setReceiveData(new String(payload));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
