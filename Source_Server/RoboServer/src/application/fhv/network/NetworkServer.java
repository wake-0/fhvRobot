/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: NetworkServer.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network;

import java.io.IOException;
import java.net.SocketException;

import communication.commands.Commands;
import communication.flags.Flags;
import controllers.ClientController;
import models.Client;
import network.communication.AppCommunication;
import network.communication.Communication;
import network.communication.CommunicationDelegator;
import network.communication.RoboCommunication;

public class NetworkServer {

	// Fields
	private final Communication roboCommunication;
	private final Communication appCommunication;
	private final CommunicationDelegator delegator;

	// Ports
	private final int roboPort = 998;
	private final int appPort = 997;

	// Constructor
	public NetworkServer(ClientController<Client> roboController, ClientController<Client> appController)
			throws SocketException {

		this.delegator = new CommunicationDelegator(roboController, appController);

		// Added network sender and receiver which can log
		this.roboCommunication = new RoboCommunication(roboController, roboPort);
		delegator.setChannelA(roboCommunication);

		this.appCommunication = new AppCommunication(appController, delegator, appPort);
		delegator.setChannelB(appCommunication);

		new Thread(roboCommunication).start();
		new Thread(appCommunication).start();
	}

	// Methods
	public void sendToRobo(Client client) {
		try {
			roboCommunication.sendToClient(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToRobo(Client client, int flags, int command, byte[] payload) {
		roboCommunication.sendToClient(client, flags, command, payload);
	}

	public void shutdown() {
		roboCommunication.stop();
		appCommunication.stop();
	}

	public void sendToApp(Client client) {
		try {
			appCommunication.sendToClient(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToApp(Client client, int flags, int command, byte[] payload) {
		appCommunication.sendToClient(client, flags, command, payload);
	}

	public void DisconnectedAppClient(Client client) {
		sendToApp(client, Flags.REQUEST_FLAG, Commands.DISCONNECTED, new byte[] { 0 });
	}

	public void DisconnectedRoboClient(Client client) {
		sendToRobo(client, Flags.REQUEST_FLAG, Commands.DISCONNECTED, new byte[] { 0 });
	}
}