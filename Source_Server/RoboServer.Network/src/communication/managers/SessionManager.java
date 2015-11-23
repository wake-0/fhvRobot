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
import java.util.concurrent.ThreadLocalRandom;

import communication.IConfiguration;
import communication.pdu.SessionPDU;
import communication.utils.ByteParser;

public class SessionManager extends LayerManager<SessionPDU> {

	// Fields
	private final int minSessionNumber = 0;
	private final int maxSessionNumber = 255;

	private final byte initConnectionFlags = (byte) 0b10000000;
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
			int newIntSession = createNewSessionNumber(currentConfiguration.getSessionId());
			byte newByteSession = ByteParser.intToByte(newIntSession);

			// Set the session id for the configuration
			currentConfiguration.setSessionId(newIntSession);

			// Create answer pdu
			byte[] answer = pdu.getEnhancedData();
			answer[0] = defaultConnectionFlags;
			answer[1] = newByteSession;

			// Send answer pdu
			sender.answer(currentConfiguration, answer);

			// Everything is handled so no need to go to the upper layers
			handled = true;
		}

		return handled;
	}

	private int createNewSessionNumber(int oldSessionNumber) {
		int newNumber = oldSessionNumber;
		{
			// Create new session number between min and max
			newNumber = ThreadLocalRandom.current().nextInt(minSessionNumber, maxSessionNumber + 1);
		}
		while (newNumber == oldSessionNumber)
			;

		return newNumber;
	}
}
