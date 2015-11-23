/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: IClientProvider.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network;

import java.util.List;

import communication.IConfiguration;
import models.Client;

public interface IClientProvider {
	public void addRoboClient(Client client);

	public void removeRoboClient(Client client);

	public List<IConfiguration> getRoboClients();

	public void addAppClient(Client client);

	public void removeAppClient(Client client);

	public List<IConfiguration> getAppClients();
}
