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
import java.util.List;
import java.util.Optional;

import communication.IConfiguration;
import communication.pdu.PDU;

public class NetworkManager extends LayerManager {

	// Constructor
	public NetworkManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, PDU pdu, IAnswerHandler sender) {
		String ipAddress = packet.getAddress().getHostName();

		List<IConfiguration> configurations = manager.getConfigurations();
		IConfiguration currentConfiguration = getConfiguration(configurations, ipAddress);

		if (currentConfiguration == null) {
			currentConfiguration = manager.createConfiguration();
			currentConfiguration.setIpAddress(ipAddress);
		}

		currentConfigurationService.setConfiguration(currentConfiguration);
		return false;
	}

	private IConfiguration getConfiguration(List<IConfiguration> configurations, String ipAddress) {
		Optional<IConfiguration> configuration = configurations.stream().filter(c -> c.getIpAddress().equals(ipAddress))
				.findFirst();
		return configuration.isPresent() ? configuration.get() : null;
	}
}
