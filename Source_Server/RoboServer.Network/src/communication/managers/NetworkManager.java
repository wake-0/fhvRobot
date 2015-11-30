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

import communication.IConfiguration;
import communication.pdu.NetworkPDU;
import communication.pdu.PDUFactory;
import communication.pdu.SessionPDU;

public class NetworkManager extends LayerManager<NetworkPDU> {

	private final int maxConfigurationCount = 128;

	// Constructor
	public NetworkManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, NetworkPDU pdu, IAnswerHandler sender) {
		String ipAddress = packet.getAddress().getHostName();

		List<IConfiguration> configurations = manager.getConfigurations();

		if (configurations.size() >= maxConfigurationCount) {
			// TODO: answer no free space
			// sender.answer(configuration, datagram);
			return true;
		}

		IConfiguration currentConfiguration = getConfiguration(configurations, ipAddress, pdu);

		if (currentConfiguration == null) {
			currentConfiguration = manager.createConfiguration();
			currentConfiguration.setIpAddress(ipAddress);
		}

		currentConfigurationService.setConfiguration(currentConfiguration);
		return false;
	}

	private IConfiguration getConfiguration(List<IConfiguration> configurations, String ipAddress, NetworkPDU pdu) {

		// TODO: Remove this hack
		SessionPDU sessionPDU = PDUFactory.createSessionPDU(PDUFactory.createTransportPDU(pdu.getData()).getData());
		int sessionId = sessionPDU.getSessionId();
		int flags = sessionPDU.getFlags();

		// It is necessary to create a new configuration
		if (sessionId == 0 && flags == 1) {
			return null;
		}

		for (IConfiguration config : configurations) {
			if (config.getIpAddress().equals(ipAddress) && config.getSessionId() == sessionId) {
				return config;
			}
		}

		return null;
	}
}
