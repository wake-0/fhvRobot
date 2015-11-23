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

public abstract class LayerManager implements IDataReceivedHandler {

	// Fields
	protected IConfigurationManager manager;
	protected CurrentConfigurationService currentConfigurationService;

	// Constructor
	public LayerManager(IConfigurationManager manager, CurrentConfigurationService currentConfigurationService) {
		this.manager = manager;
		this.currentConfigurationService = currentConfigurationService;
	}
}
