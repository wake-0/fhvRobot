/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: NetworkManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;

import communication.configurations.IConfiguration;
import communication.pdu.NetworkPDU;

public class NetworkManager<T extends IConfiguration> extends LayerManager<NetworkPDU, T> {

	// Constructor
	public NetworkManager(IConfigurationManager<T> clientManager, TempConfigurationsService currentClientService) {
		super(clientManager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, NetworkPDU pdu, IConfiguration configuration,
			IAnswerHandler sender) {

		String ipAddress = packet.getAddress().toString().substring(1);
		configuration.setIpAddress(ipAddress);
		configuration.setSocketAddress(packet.getSocketAddress());

		// Heart beat increase
		configuration.increaseHeartBeatCount();
		return false;
	}
}
