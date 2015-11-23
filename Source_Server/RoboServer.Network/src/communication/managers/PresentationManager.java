/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: PresentationManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;

import communication.pdu.PDU;

public class PresentationManager extends LayerManager {

	// Constructor
	public PresentationManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, PDU pdu, IAnswerHandler sender) {
		// TODO: e.g. add checksum
		return false;
	}
}
