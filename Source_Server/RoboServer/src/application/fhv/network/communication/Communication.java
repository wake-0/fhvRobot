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
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
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

	public Communication(CommunicationManager manager, int port) throws SocketException {
		this.manager = manager;
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
