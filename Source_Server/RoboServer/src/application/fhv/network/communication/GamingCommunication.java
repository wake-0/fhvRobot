package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.List;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import controllers.PersistencyController;
import models.Client;
import network.IClientController;

public class GamingCommunication extends Communication {

	// Constructor
	public GamingCommunication(IClientController<Client> clientController, int port,
			PersistencyController persistencyController) throws SocketException {
		super(clientController, port, persistencyController);
	}

	// Methods
	@Override
	protected boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client) {
		try {

			byte[] payload = pdu.getPayload();
			System.out.println("Gaming communication called! [" + new String(payload) + "]");

			int command = pdu.getCommand();
			switch (command) {
			case Commands.REQUEST_OPERATOR:
				List<Client> operators = clientController.getOperators();
				String name = "";
				if (!operators.isEmpty()) {
					name = operators.get(0).getName();
				}

				DatagramPacket datagram = manager.createDatagramPacket(client, Flags.ANSWER_FLAG,
						Commands.REQUEST_OPERATOR, name.getBytes());
				sender.answer(datagram);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

}
