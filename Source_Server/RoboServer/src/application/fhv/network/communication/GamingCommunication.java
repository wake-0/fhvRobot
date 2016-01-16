package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import controllers.OperatorManager;
import controllers.PersistencyController;
import models.Client;
import network.IClientController;

public class GamingCommunication extends Communication {

	private OperatorManager operatorManager;

	// Constructor
	public GamingCommunication(IClientController<Client> clientController, int port,
			PersistencyController persistencyController) throws SocketException {
		super(clientController, port, persistencyController);
	}

	public GamingCommunication(IClientController<Client> gamingController, int gamingPort,
			OperatorManager operatorManager, PersistencyController persistencyController) throws SocketException {
		this(gamingController, gamingPort, persistencyController);
		this.operatorManager = operatorManager;
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
				DatagramPacket datagram = manager.createDatagramPacket(client, Flags.ANSWER_FLAG,
						Commands.REQUEST_OPERATOR, operatorManager.getOperatorName().getBytes());
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
