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

import communication.IConfiguration;
import communication.pdu.SessionPDU;
import communication.utils.NumberParser;

public class SessionManager extends LayerManager<SessionPDU> {

	// Fields
	private final int minSessionNumber = 1;
	private final int maxSessionNumber = 127;

	private final byte initConnectionFlags = (byte) 0b00000001;
	private final byte initConnectionSession = (byte) 0b00000000;
	private final byte defaultConnectionFlags = (byte) 0b00000000;

	// Constructor
	public SessionManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, SessionPDU pdu, IAnswerHandler sender) {
		IConfiguration currentConfiguration = currentConfigurationService.getConfiguration();
		boolean handled = false;

		byte[] data = pdu.getData();
		byte flags = pdu.getFlags();
		int sessionId = pdu.getSessionId();

		// TODO: e.g. add session checking for security

		// Only create a new session id at the beginning --> this is not save
		if (flags == initConnectionFlags && sessionId == initConnectionSession) {

			List<Integer> alreadyUsedSessions = new ArrayList<>();
			for (IConfiguration configuration : manager.getConfigurations()) {
				alreadyUsedSessions.add(configuration.getSessionId());
			}

			// TODO: create a new create SessionNumberAlgorithm
			// because the old one is not checking the already used session ids
			int newIntSession = createNewSessionNumber(currentConfiguration.getSessionId(), alreadyUsedSessions);
			byte newByteSession = NumberParser.intToByte(newIntSession);

			// Set the session id for the configuration
			currentConfiguration.setSessionId(newIntSession);

			// Create answer pdu
			byte answerFlags = initConnectionFlags;
			byte answerSessionId = newByteSession;
			byte[] answer = new byte[] { answerFlags, answerSessionId };

			// Send answer pdu
			sender.answer(currentConfiguration, answer);

			// Everything is handled so no need to go to the upper layers
			handled = true;
		} else if (flags == initConnectionFlags && sessionId != initConnectionSession) {
			// Set session id
			currentConfiguration.setSessionId(pdu.getSessionId());
			handled = true;
		}

		return handled;
	}

	private int createNewSessionNumber(int oldSessionNumber, List<Integer> notAllowedSessions) {
		int newNumber = oldSessionNumber;
		{
			// Create new session number between min and max
			newNumber = ThreadLocalRandom.current().nextInt(minSessionNumber, maxSessionNumber + 1);
		}
		while (newNumber == oldSessionNumber || notAllowedSessions.contains(newNumber))
			;

		return newNumber;
	}
}
