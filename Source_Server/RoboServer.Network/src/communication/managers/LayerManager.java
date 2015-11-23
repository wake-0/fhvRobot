/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: LayerManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import communication.pdu.PDU;

public abstract class LayerManager<T extends PDU> implements IDataReceivedHandler<T> {

	// Fields
	protected IConfigurationManager manager;
	protected CurrentConfigurationService currentConfigurationService;

	// Constructor
	public LayerManager(IConfigurationManager manager, CurrentConfigurationService currentConfigurationService) {
		this.manager = manager;
		this.currentConfigurationService = currentConfigurationService;
	}
}
