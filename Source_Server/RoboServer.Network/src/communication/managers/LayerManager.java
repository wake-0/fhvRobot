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

import communication.configurations.IConfiguration;
import communication.pdu.PDU;

public abstract class LayerManager<T extends PDU, E extends IConfiguration> implements IDataReceivedHandler<T, E> {

	// Fields
	protected IConfigurationManager<E> manager;
	protected TempConfigurationsService currentConfigurationService;

	// Constructor
	public LayerManager(IConfigurationManager<E> manager, TempConfigurationsService currentConfigurationService) {
		this.manager = manager;
		this.currentConfigurationService = currentConfigurationService;
	}
}
