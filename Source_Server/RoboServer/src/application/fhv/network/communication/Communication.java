/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: Communication.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.heartbeat.HeartbeatProvider;
import communication.heartbeat.IHeartbeatCreator;
import communication.managers.CommunicationManager;
import communication.managers.DatagramFactory;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
import controllers.ClientController.IOperatorChangedListener;
import controllers.ClientNameController;
import controllers.PersistencyController;
import models.Client;
import models.Orientation3D;
import network.IClientController;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

public abstract class Communication implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler,
		IHeartbeatCreator, IOperatorChangedListener<Client> {

	// Field
	private boolean isRunning;

	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;
	protected final CommunicationManager manager;
	protected final IClientController<Client> clientController;
	protected final HeartbeatProvider heartbeatProvider;
	protected final ClientNameController nameController;
	protected final PersistencyController persistencyController;

	// Constructors
	public Communication(IClientController<Client> clientController, int port,
			PersistencyController persistencyController) throws SocketException {
		this.clientController = clientController;
		this.manager = new CommunicationManager(clientController);
		this.socket = new DatagramSocket(port);

		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
		this.nameController = new ClientNameController();

		this.heartbeatProvider = new HeartbeatProvider(this);
		this.heartbeatProvider.run();

		this.persistencyController = persistencyController;
	}

	// Methods
	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			byte[] receiveData = new byte[CommunicationSettings.RECEIVE_PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			receiver.receive(receivePacket);

			if (socket.isClosed()) {
				continue;
			}

			manager.readDatagramPacket(receivePacket, this, this);
		}
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender) {

		Client client = (Client) manager.getCurrentConfiguration();
		byte[] payload = pdu.getPayload();
		int command = pdu.getCommand();
		boolean handled = false;

		// This means register name
		if (command == Commands.CHANGE_NAME) {

			String name = new String(payload);

			// check if name is duplicated
			name = nameController.findSimilarName(name);
			nameController.registerName(name);

			client.setName(name);
			DatagramPacket datagram = DatagramFactory.createDatagramPacket(client, Flags.ANSWER_FLAG,
					Commands.CHANGE_NAME, name.getBytes());
			sender.answer(datagram);
			handled = true;

		} else if (command == Commands.REQUEST_DISCONNECT) {

			clientController.removeClient(client);
			handled = true;

		} else if (command == Commands.PERSIST_DATA) {
			persistencyController.setPersistentData(payload);
			handled = true;

		} else if (command == Commands.REQUEST_PERSISTED_DATA) {

			DatagramPacket datagram = DatagramFactory.createDatagramPacket(client, Flags.ANSWER_FLAG,
					Commands.REQUEST_PERSISTED_DATA, persistencyController.getPersistentData());
			sender.answer(datagram);
			handled = true;
		} else if (command == Commands.ORIENTATION_DATA) {
			if (payload.length == 6) {
				short roll = (short) ((payload[0] << 8) | payload[1]);
				short pitch = (short) ((payload[2] << 8) | payload[3]);
				short yaw = (short) ((payload[4] << 8) | payload[5]);
				client.setOrientation(new Orientation3D(roll, pitch, yaw));
			}
		}

		if (!handled) {
			handled = handleDataReceivedCore(packet, pdu, sender, client);
		}

		// Set the received data
		client.setReceiveData(new String(payload));

		return handled;
	}

	protected abstract boolean handleDataReceivedCore(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender,
			Client client);

	public void sendToClient(Client client, int flags, int command, byte[] payload) {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = DatagramFactory.createDatagramPacket(client, flags, command, payload);
		sender.send(sendPacket);
	}

	public void sendToClient(Client client) throws IOException {
		int command = Commands.GENERAL_MESSAGE;
		int flags = Flags.REQUEST_FLAG;
		byte[] data = client.getSendData().getBytes();
		sendToClient(client, flags, command, data);
	}

	@Override
	public void answer(DatagramPacket datagram) {
		sender.send(datagram);
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}

	public void createHeartBeat() {
		try {
			for (Client client : clientController.getClients()) {
				DatagramPacket heartbeatPacket = DatagramFactory.createHeartbeatDatagramPacket(client);
				socket.send(heartbeatPacket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleOperatorChanged(Client operator) {
		if (operator == null) {
			return;
		}

		int command = operator.getIsOperator() ? Commands.ROBO_STEARING : Commands.ROBO_NOT_STEARING;
		sendToClient(operator, Flags.REQUEST_FLAG, command, new byte[] { 0 });
	}
}
