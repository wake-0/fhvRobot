package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import models.Client;
import network.IClientController;

public class GamingCommunication extends Communication {

	// Constructor
	public GamingCommunication(IClientController<Client> clientController, int port) throws SocketException {
		super(clientController, port);
	}

	// Methods
	@Override
	protected boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client) {
		try {

			byte[] payload = pdu.getPayload();
			System.out.println("Gaming communication called! [" + new String(payload) + "]");

			DatagramPacket datagram = manager.createDatagramPacket(client, Flags.ANSWER_FLAG, Commands.GENERAL_MESSAGE,
					payload);
			sender.answer(datagram);

			// byte[] payload = pdu.getPayload();
			// int command = pdu.getCommand();
			// int flags = pdu.getFlags();
			//
			// // Only for test purposes
			// client.setSendData(new String(payload));
			// clientController.handleCommandReceived(client, command, payload);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

}
