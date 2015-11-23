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

public interface IClientProvider<T extends IConfiguration> {

	public void addClient(T client);

	public void removeClient(T client);

	public List<T> getClients();
}
