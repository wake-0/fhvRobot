
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import communication.commands.Commands;
import communication.configurations.Configuration;
import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.flags.Flags;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import communication.managers.CommunicationManager;
import communication.managers.DatagramFactory;
import communication.managers.IAnswerHandler;
import communication.managers.IConfigurationManager;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;

public class UDPClient implements Runnable, IDataReceivedHandler<ApplicationPDU, IConfiguration>, IAnswerHandler,
		IConfigurationManager, IHeartbeatHandler<IConfiguration> {

	private DatagramSocket clientSocket;
	private int sessionId = ConfigurationSettings.DEFAULT_SESSION_ID;
	private int port = UDPClientSettings.SOCKET_PORT;
	private String address = UDPClientSettings.DEFAULT_ADDRESS;

	private CommunicationManager manager;
	private IConfiguration configuration;
	private List<IConfiguration> configurations;

	private HeartbeatManager<IConfiguration> heartBeatManager;
	private static final int HEARTBEAT_TIME = 1 * 1000;

	private int flow = 0;

	public UDPClient() {
		try {
			manager = new CommunicationManager(this);
			clientSocket = new DatagramSocket();

			this.configuration = new Configuration(sessionId, port, address);
			this.configurations = new ArrayList<>();
			configurations.add(configuration);

			this.heartBeatManager = new HeartbeatManager<IConfiguration>(configuration, this, HEARTBEAT_TIME,
					HEARTBEAT_TIME);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {

			try {

				DatagramPacket sendPacket;
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String sentence = inFromUser.readLine();

				System.out.println("Send message:" + sentence);

				if (flow == 0) {
					System.out.println("Open connection called.");
					sendPacket = DatagramFactory.createOpenConnectionDatagramPacket(configuration);
					this.heartBeatManager.run();
				} else if (flow == 1) {
					System.out.println("Change name called.");
					sendPacket = DatagramFactory.createDatagramPacket(configuration, Flags.REQUEST_FLAG,
							Commands.CHANGE_NAME, sentence.getBytes());
				} else {
					sendPacket = DatagramFactory.createDatagramPacket(configuration, Flags.REQUEST_FLAG,
							Commands.GENERAL_MESSAGE, sentence.getBytes());
				}
				System.out.println("Enhanced send message:" + new String(sendPacket.getData()));

				clientSocket.send(sendPacket);

				byte[] receiveData = new byte[256];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);

				System.out.println("Receive message:" + new String(receivePacket.getData()));
				manager.readDatagramPacket(receivePacket, this, this);

				// use new configuration because session id is set correct

				flow++;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		// clientSocket.close();
	}

	@Override
	public void answer(DatagramPacket packet) {

	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, ApplicationPDU pdu, IConfiguration configuration,
			IAnswerHandler sender) {

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
		return configurations;
	}

	@Override
	public void handleNoHeartbeat(IConfiguration configuration) {
		heartBeat();
	}

	@Override
	public void handleHeartbeat(IConfiguration configuration) {
		heartBeat();
	}

	private void heartBeat() {
		DatagramPacket sendPacket = DatagramFactory.createDatagramPacket(configuration, Flags.REQUEST_FLAG,
				Commands.DEFAULT, "0".getBytes());
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
