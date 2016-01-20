/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: CurrentConfigurationService.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import communication.configurations.Configuration;
import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.NetworkPDU;
import communication.pdu.PDUFactory;
import communication.pdu.SessionPDU;
import communication.pdu.TransportPDU;

public class TempConfigurationsService {

	// Fields
	private List<IConfiguration> configurations;

	// Constructor
	public TempConfigurationsService() {
		configurations = new ArrayList<>();
	}

	// Methods
	public IConfiguration findConfiguration(DatagramPacket packet, byte[] data) {

		NetworkPDU pdu = PDUFactory.createNetworkPDU(data);
		// Try find a configuration
		// String ipAddress = packet.getAddress().getHostName(); NOTE: This call
		// is super-slow as it performs a DNS lookup
		// See
		// http://stackoverflow.com/questions/11795167/datagrampacket-getaddress-gethostname-is-blocking-my-thread
		// Better is to get the ip as follows:
		String ipAddress = packet.getAddress().toString().substring(1);

		// This is a hack for checking session id is already used
		TransportPDU transportPDU = PDUFactory.createTransportPDU(pdu.getInnerData());
		if (transportPDU == null) {
			return null;
		}

		SessionPDU sessionPDU = PDUFactory.createSessionPDU(transportPDU.getInnerData());
		if (sessionPDU == null) {
			return null;
		}

		int sessionId = sessionPDU.getSessionId();
		int flags = sessionPDU.getFlags();

		// It is necessary to create a new configuration
		if (sessionId == ConfigurationSettings.DEFAULT_SESSION_ID
				&& flags == ConfigurationSettings.REQUEST_SESSION_FLAGS) {
			return null;
		}

		return getConfiguration(ipAddress, sessionId);
	}

	private IConfiguration getConfiguration(String ipAddress, int sessionId) {
		for (IConfiguration config : configurations) {
			if (config.getIpAddress().equals(ipAddress) && config.getSessionId() == sessionId) {
				return config;
			}
		}

		return null;
	}

	public IConfiguration createConfiguration() {
		IConfiguration configuaration = new Configuration(0, 0, "127.0.0.1");
		configurations.add(configuaration);
		return configuaration;
	}
}
