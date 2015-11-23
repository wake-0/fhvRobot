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

import communication.managers.CommunicationManager;
import controllers.ClientController;
import models.Client;

public class NetworkServer {

	// Fields
	private final Communication roboCommunication;
	private final Communication appCommunication;

	// Ports
	private final int roboPort = 997;
	private final int appPort = 998;

	// Constructor
	public NetworkServer(ClientController<Client> roboController, ClientController<Client> appController)
			throws SocketException {
		// Added network sender and receiver which can log
		this.roboCommunication = new Communication(new CommunicationManager(roboController), roboPort);
		new Thread(roboCommunication).start();

		this.appCommunication = new Communication(new CommunicationManager(appController), appPort);
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

	public void shutdown() {
		roboCommunication.stop();
		appCommunication.stop();
	}
}