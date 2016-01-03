/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: SessionManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import communication.configurations.Configuration;
import communication.configurations.ConfigurationFactory;
import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.SessionPDU;

public class SessionManager extends LayerManager<SessionPDU> {

	// Init ThreadLocalRandom
	// NOTE: See Ticket #68: ThreadLocalRandom's first call is sometimes slow on some machines
	{
		ThreadLocalRandom.current().nextInt(1, 1000);
	}

	// Fields
	private final int maxIterations = 200;

	// Constructor
	public SessionManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, SessionPDU pdu, IAnswerHandler sender) {
		IConfiguration configuration = currentConfigurationService.getConfiguration();

		boolean handled = false;

		// Data from the packet
		// byte[] data = pdu.getData();
		byte flags = pdu.getFlags();
		int sessionId = pdu.getSessionId();

		int requestSessionIdFlags = ConfigurationSettings.REQUEST_SESSION_FLAGS;
		int defaultSession = ConfigurationSettings.DEFAULT_SESSION_ID;

		// Only create a new session id at the beginning, a better solution
		// would be always creating a new session
		if (flags == requestSessionIdFlags && sessionId == defaultSession) {

			int oldSession = configuration.getSessionId();
			int newSession = createNewSessionNumber(oldSession, getAlreadyUsedSessionIds());

			if (newSession == ConfigurationSettings.NOT_ALLOWED_SESSION_ID) {

				// Send rejected as answer
				Configuration answerConfiguration = ConfigurationFactory.createConfiguration(packet);
				DatagramPacket answerPacket = DatagramFactory.createNoFreeSlotPacket(answerConfiguration);
				sender.answer(answerPacket);

			} else {

				// Set the session id for the configuration
				configuration.setSessionId(newSession);

				// Send answer pdu
				DatagramPacket answerPacket = DatagramFactory.createSessionPacket(configuration, newSession);
				sender.answer(answerPacket);
			}

			// Everything is handled so no need to go to the upper layers
			handled = true;

		} else if (flags == ConfigurationSettings.REQUEST_SESSION_FLAGS
				&& sessionId != ConfigurationSettings.DEFAULT_SESSION_ID) {

			// Set session id
			configuration.setSessionId(sessionId);
			handled = true;
		}

		return handled;
	}

	private int createNewSessionNumber(int oldSessionNumber, List<Integer> notAllowedSessions) {
		int newSession = oldSessionNumber;
		int minSession = ConfigurationSettings.MIN_SESSION_NUMBER;
		int maxSession = ConfigurationSettings.MAX_SESSION_NUMBER + 1;
		int notAllowedSession = ConfigurationSettings.NOT_ALLOWED_SESSION_ID;

		int numberOfIterations = 0;
		do {
			// Create new session number between min and max
			newSession = ThreadLocalRandom.current().nextInt(minSession, maxSession);

			// Break condition
			numberOfIterations++;
		} while ((newSession == oldSessionNumber || notAllowedSessions.contains(newSession))
				&& numberOfIterations < maxIterations);

		return numberOfIterations >= maxIterations ? notAllowedSession : newSession;
	}

	private List<Integer> getAlreadyUsedSessionIds() {
		List<Integer> alreadyUsedSessions = new ArrayList<>();

		for (IConfiguration configuration : manager.getConfigurations()) {
			alreadyUsedSessions.add(configuration.getSessionId());
		}

		return alreadyUsedSessions;
	}
}
