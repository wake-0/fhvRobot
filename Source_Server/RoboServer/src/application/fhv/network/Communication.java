/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: Communication.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import communication.IConfiguration;
import communication.commands.Commands;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
import models.Client;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

public class Communication implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler {

	private boolean isRunning;
	private int receivePacketSize = 1024;
	private final INetworkReceiver receiver;
	private final INetworkSender sender;
	private final DatagramSocket socket;
	private final CommunicationManager manager;
	private final CommunicationDelegator delegator;

	public Communication(CommunicationManager manager, CommunicationDelegator delegator, int port)
			throws SocketException {
		this.manager = manager;
		this.delegator = delegator;
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

				if (delegator != null) {
					delegator.DelegateMessage(this, command, payload);
				}
			}

			// TODO: handle other message
			client.setReceiveData(new String(payload));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void answer(IConfiguration configuration, byte[] data) {
		InetAddress address;
		try {
			address = InetAddress.getByName(configuration.getIpAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			address = InetAddress.getLoopbackAddress();
		}

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
