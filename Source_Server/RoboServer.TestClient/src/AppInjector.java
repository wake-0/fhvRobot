

import com.google.inject.AbstractModule;

import communication.managers.CommunicationManager;
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
	}
	
}
