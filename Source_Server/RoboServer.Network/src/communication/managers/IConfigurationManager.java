/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: IConfigurationManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.util.List;

import communication.configurations.IConfiguration;

public interface IConfigurationManager<T extends IConfiguration> {

	public T createConfiguration();

	public List<T> getConfigurations();
}
