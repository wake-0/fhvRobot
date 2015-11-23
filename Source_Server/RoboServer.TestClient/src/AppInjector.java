/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.TestClient
 * Filename: AppInjector.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */

import com.google.inject.AbstractModule;

import communication.managers.CommunicationManager;
import communication.managers.IConfigurationManager;
import communication.managers.IDataReceivedHandler;
import communication.managers.NetworkManager;
import communication.managers.SessionManager;
import communication.managers.TransportManager;

public class AppInjector extends AbstractModule {

	@Override
	protected void configure() {
		bind(NetworkManager.class);
		bind(TransportManager.class);
		bind(SessionManager.class);
		bind(CommunicationManager.class);

		bind(UDPClient.class);
		bind(IConfigurationManager.class).to(UDPClient.class);
		bind(IDataReceivedHandler.class).to(UDPClient.class);
	}

}
