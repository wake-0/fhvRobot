package network.communication;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.List;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.managers.DatagramFactory;
import communication.managers.IAnswerHandler;
import communication.pdu.ApplicationPDU;
import controllers.IOperatorManager;
import controllers.PersistencyController;
import models.Client;
import network.IClientController;

public class GamingCommunication extends Communication {

	private final IOperatorManager<Client> operatorManager;

	// Constructor
	public GamingCommunication(IClientController<Client> clientController, Delegator delegator, int port,
			PersistencyController persistencyController, IOperatorManager<Client> operatorManager)
					throws SocketException {
		super(clientController, delegator, port, persistencyController);
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
				List<Client> operators = operatorManager.getOperators();
				String name = "";
				if (!operators.isEmpty()) {
					name = operators.get(0).getName();
				}

				DatagramPacket datagram = DatagramFactory.createDatagramPacket(client, Flags.ANSWER_FLAG,
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

	@Override
	protected boolean isCommandToDelegate(int command) {
		return command == Commands.FORWARD_GENERAL_MESSAGE || command == Commands.TIME_MEASUREMENT_STARTED
				|| command == Commands.TIME_MEASUREMENT_STOPPED || command == Commands.TIME_MEASUREMENT_DISMISSED;
	}

}
