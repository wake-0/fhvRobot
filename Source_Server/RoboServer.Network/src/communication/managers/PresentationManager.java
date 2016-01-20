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

import communication.configurations.IConfiguration;
import communication.pdu.PresentationPDU;

public class PresentationManager<T extends IConfiguration> extends LayerManager<PresentationPDU, T> {

	// Constructor
	public PresentationManager(IConfigurationManager<T> clientManager, TempConfigurationsService currentClientService) {
		super(clientManager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, PresentationPDU pdu, IConfiguration configuration,
			IAnswerHandler sender) {
		return false;
	}
}
