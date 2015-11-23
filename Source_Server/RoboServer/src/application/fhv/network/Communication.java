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
import java.net.UnknownHostException;
import java.util.Arrays;

import communication.IConfiguration;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.PDU;
import models.Client;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

public class Communication implements Runnable, IDataReceivedHandler, IAnswerHandler {

	private boolean isRunning;
	private int receivePacketSize = 1024;
	private final INetworkReceiver receiver;
	private final INetworkSender sender;
	private final DatagramSocket socket;
	private final CommunicationManager manager;

	public Communication(CommunicationManager manager, DatagramSocket socket) {
		this.manager = manager;
		this.socket = socket;
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
	public boolean handleDataReceived(DatagramPacket packet, PDU pdu, IAnswerHandler sender) {
		try {

			byte[] data = pdu.getData();
			byte flags = data[0];
			byte command = data[1];
			byte length = data[2];

			// TODO: check length and real payload are equal
			byte[] payload = Arrays.copyOfRange(data, 3, data.length);
			String name = new String(payload);

			Client client = (Client) manager.getCurrentConfiguration();
			client.setName(name);
			client.setReceiveData(name);

			// TODO: handle other message

			// Only for test purposes
			client.setSendData(name);
			sendToClient(client);

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

	public void sendToClient(Client client) throws IOException {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = manager.createDatagramPacket(client, client.getSendData());
		sender.send(sendPacket);
	}

	public void stop() {
		isRunning = false;
	}
}
