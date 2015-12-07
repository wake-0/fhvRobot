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
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
import controllers.ClientController;
import models.Client;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

public abstract class Communication implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler {

	// Field
	private boolean isRunning;

	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;
	protected final CommunicationManager manager;
	protected final ClientController<Client> clientController;

	// Constructors
	public Communication(ClientController<Client> clientController, int port) throws SocketException {
		this.clientController = clientController;
		this.manager = new CommunicationManager(clientController);
		this.socket = new DatagramSocket(port);

		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
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
			client.setName(name);

			DatagramPacket datagram = manager.createDatagramPacket(client, Commands.CHANGE_NAME, new byte[] { 1 });
			sender.answer(datagram);
			handled = true;

		} else if (command == Commands.REQUEST_DISCONNECT) {

			clientController.removeClient(client);
			handled = true;

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

	public void sendToClient(Client client, int command, byte[] payload) {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = manager.createDatagramPacket(client, command, payload);
		sender.send(sendPacket);
	}

	public void sendToClient(Client client) throws IOException {
		int command = Commands.GENERAL_MESSAGE;
		byte[] data = client.getSendData().getBytes();
		sendToClient(client, command, data);
	}

	@Override
	public void answer(DatagramPacket datagram) {
		sender.send(datagram);
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}
}
