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

public class TransportManager<T extends IConfiguration> extends LayerManager<TransportPDU, T> {

	// Constructor
	public TransportManager(IConfigurationManager<T> clientManager, TempConfigurationsService currentClientService) {
		super(clientManager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, TransportPDU pdu, IConfiguration configuration,
			IAnswerHandler sender) {

		int port = packet.getPort();
		configuration.setPort(port);

		return false;
	}
}
