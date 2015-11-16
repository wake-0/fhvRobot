package controllers;

import com.google.inject.AbstractModule;

import communication.managers.CommunicationManager;
import communication.managers.IClientManager;
import communication.managers.IDataReceivedHandler;
import communication.managers.NetworkManager;
import communication.managers.SessionManager;
import communication.managers.TransportManager;
import models.ClientFactory;
import network.IClientProvider;
import network.NetworkServer;

public class AppInjector extends AbstractModule {

	private IClientProvider clientProvider;
	
	public AppInjector(IClientProvider provider) {
		this.clientProvider = provider;
	}
	
	@Override
	protected void configure() {
		
		bind(NetworkManager.class);
		bind(TransportManager.class);
		bind(SessionManager.class);
		bind(CommunicationManager.class);
		
		bind(NetworkServer.class);
		bind(ClientFactory.class);
		bind(IDataReceivedHandler.class).to(NetworkServer.class);
		bind(IClientManager.class).to(NetworkServer.class);
		
		bind(IClientProvider.class).toInstance(clientProvider);
	}
	
}
