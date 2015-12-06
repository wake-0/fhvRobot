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
import java.net.InetAddress;
import java.net.SocketException;

import communication.IConfiguration;
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
import utils.InetParser;

public abstract class Communication implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler {

	private boolean isRunning;
	private int receivePacketSize = 1024;
	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;
	protected final CommunicationManager manager;
	protected final ClientController<Client> clientController;

	public Communication(ClientController<Client> clientController, int port) throws SocketException {
		this.clientController = clientController;
		this.manager = new CommunicationManager(clientController);
		this.socket = new DatagramSocket(port);
		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
	}

	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			byte[] receiveData = new byte[receivePacketSize];
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

			// Create answer pdu
			// byte[] nameBytes = Arrays.copyOfRange(payload, 0,
			// pdu.getPayloadLength());

			DatagramPacket datagram = manager.createDatagramPacket(client, 1, new byte[] { 1 });
			sender.answer(client, datagram);
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

	@Override
	public void answer(IConfiguration configuration, byte[] data) {
		InetAddress address = InetParser.parseStringToInetAddress(configuration.getIpAddress());
		int port = configuration.getPort();
		DatagramPacket answerPacket = new DatagramPacket(data, data.length, address, port);
		sender.send(answerPacket);
	}

	public void sendToClient(Client client, int command, byte[] payload) {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = manager.createDatagramPacket(client, command, payload);
		sender.send(sendPacket);
	}

	public void sendToClient(Client client) throws IOException {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = manager.createDatagramPacket(client, client.getSendData());
		sender.send(sendPacket);
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}

	@Override
	public void answer(IConfiguration configuration, DatagramPacket datagram) {
		// TODO: compare configuration and datagram
		sender.send(datagram);
	}
}
