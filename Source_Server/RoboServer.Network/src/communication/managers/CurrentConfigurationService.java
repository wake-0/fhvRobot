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

import communication.IConfiguration;

public class CurrentConfigurationService {

	// Fields
	private IConfiguration configuration;

	// Constructor
	IConfiguration getConfiguration() {
		return configuration;
	}

	// Methods
	void setConfiguration(IConfiguration configuration) {
		this.configuration = configuration;
	}

}
