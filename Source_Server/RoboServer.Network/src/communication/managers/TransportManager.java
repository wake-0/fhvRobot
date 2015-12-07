/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: TransportManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;

import communication.configurations.IConfiguration;
import communication.pdu.TransportPDU;

public class TransportManager extends LayerManager<TransportPDU> {

	// Constructor
	public TransportManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, TransportPDU pdu, IAnswerHandler sender) {

		IConfiguration client = currentConfigurationService.getConfiguration();
		int port = packet.getPort();
		client.setPort(port);

		return false;
	}
}
