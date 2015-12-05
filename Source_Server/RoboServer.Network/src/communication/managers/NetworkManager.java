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
			// No free slot answer
			DatagramPacket answerPacket = createNoFreeSlotPacket();
			sender.answer(new Configuration(0, packet.getPort(), packet.getAddress().getHostName()), answerPacket);
			return true;
		}

		IConfiguration currentConfiguration = getConfiguration(configurations, ipAddress, pdu);

		if (currentConfiguration == null) {
			currentConfiguration = manager.createConfiguration();
			currentConfiguration.setIpAddress(ipAddress);
		}

		currentConfiguration.increaseHeartBeatCount();
		currentConfigurationService.setConfiguration(currentConfiguration);
		return false;
	}

	private DatagramPacket createNoFreeSlotPacket() {
		byte answerFlags = 0;
		byte answerSessionId = 0;
		byte[] answer = new byte[] { answerFlags, answerSessionId };
		DatagramPacket answerPacket = new DatagramPacket(answer, answer.length);
		return answerPacket;
	}

	private IConfiguration getConfiguration(List<IConfiguration> configurations, String ipAddress, NetworkPDU pdu) {

		// TODO: Remove this hack
		SessionPDU sessionPDU = PDUFactory.createSessionPDU(PDUFactory.createTransportPDU(pdu.getData()).getData());
		int sessionId = sessionPDU.getSessionId();
		int flags = sessionPDU.getFlags();

		// TODO: Add check for allowed session id

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

	private class Configuration implements IConfiguration {

		private int sessionId;
		private int port;
		private String ipAddress;
		private int heartBeatCount;

		public Configuration(int sessionId, int port, String ipAddress) {
			this.sessionId = sessionId;
			this.port = port;
			this.ipAddress = ipAddress;
			this.heartBeatCount = 0;
		}

		@Override
		public void setSessionId(int sessionId) {
			this.sessionId = sessionId;
		}

		@Override
		public int getSessionId() {
			return sessionId;
		}

		@Override
		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		@Override
		public String getIpAddress() {
			return ipAddress;
		}

		@Override
		public void setPort(int port) {
			this.port = port;
		}

		@Override
		public int getPort() {
			return port;
		}

		@Override
		public int getHeartBeatCount() {
			return heartBeatCount;
		}

		@Override
		public void increaseHeartBeatCount() {
			heartBeatCount++;
		}

		@Override
		public void cleanHeartBeatCount() {
			heartBeatCount = 0;
		}
	}
}
