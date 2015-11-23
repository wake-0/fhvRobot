/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: ClientFactory.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package models;

import com.google.inject.Singleton;

@Singleton
public class ClientFactory {

	public static Client createClient(String ipAddress, int port) {
		Client client = new Client();
		client.setIpAddress(ipAddress);
		client.setPort(port);
		return client;
	}

	public static Client createClient(String name) {
		Client client = new Client();
		client.setName(name);
		return client;
	}

	public static Client createClient(int sessionId) {
		Client client = new Client();
		client.setSessionId(sessionId);
		return client;
	}

}
