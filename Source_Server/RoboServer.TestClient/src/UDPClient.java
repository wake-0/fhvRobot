
/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.TestClient
 * Filename: UDPClient.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import communication.IConfiguration;
import communication.commands.Commands;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IConfigurationManager;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;

public class UDPClient
		implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler, IConfigurationManager {

	private String address = "127.0.0.1";
	// private String address = "83.212.127.13";
	private int port = 998;
	private DatagramSocket clientSocket;
	private int sessionId = 0b00000000;

	private CommunicationManager manager;
	private IConfiguration configuration;

	private int flow = 0;

	public UDPClient() {
		try {
			manager = new CommunicationManager(this);
			clientSocket = new DatagramSocket();

			this.configuration = new ClientConfiguration();
			configuration.setIpAddress(address);
			configuration.setPort(port);
			configuration.setSessionId(sessionId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {

			try {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String sentence = inFromUser.readLine();

				System.out.println("Send message:" + sentence);
				DatagramPacket sendPacket;

				if (flow == 0) {
					System.out.println("Open connection called.");
					sendPacket = manager.createOpenConnectionDatagramPacket(configuration, sentence);
				} else if (flow == 1) {
					System.out.println("Change name called.");
					sendPacket = manager.createDatagramPacket(configuration, Commands.CHANGE_NAME, sentence.getBytes());
				} else {
					sendPacket = manager.createDatagramPacket(configuration, sentence);
				}
				System.out.println("Enhanced send message:" + new String(sendPacket.getData()));

				clientSocket.send(sendPacket);

				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);

				System.out.println("Receive message:" + new String(receivePacket.getData()));
				manager.readDatagramPacket(receivePacket, this, this);

				// use new configuration because session id is set correct
				configuration = manager.getCurrentConfiguration();

				flow++;
			} catch (Exception ex) {

			}
		}
		// clientSocket.close();
	}

	@Override
	public void answer(IConfiguration configuration, byte[] data) {

	}

	@Override
	public void answer(IConfiguration configuration, DatagramPacket datagram) {

	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, ApplicationPDU pdu, IAnswerHandler sender) {

		byte[] payload = pdu.getPayload();
		String name = new String(payload);

		System.out.println("Enhanced receive message:" + name);
		return false;
	}

	@Override
	public IConfiguration createConfiguration() {
		return configuration;
	}

	@Override
	public List<IConfiguration> getConfigurations() {
		return new ArrayList<>();
	}

}
