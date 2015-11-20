package network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClientConfiguration;
import communication.managers.CommunicationManager;
import communication.managers.IClientManager;
import models.Client;

@Singleton
public class NetworkServer {

	// field which stores the clients
	private final CommunicationManager communicationManagerRobo;
	private final DatagramSocket roboSocket;
	private final Communication roboCommunication;
	private final Thread roboThread;

	private final CommunicationManager communicationManagerApp;
	private final DatagramSocket appSocket;
	private final Communication appCommunication;
	private final Thread appThread;

	// server specific stuff
	private final int roboPort = 997;
	private final int appPort = 998;

	// constructors
	@Inject
	public NetworkServer(IClientProvider clientProvider) throws SocketException {
		this.roboSocket = new DatagramSocket(roboPort);
		// Added network sender and receiver which can log
		this.communicationManagerRobo = new CommunicationManager(new IClientManager() {

			@Override
			public IClientConfiguration createClientConfiguration() {
				Client client = new Client();
				clientProvider.addRoboClient(client);
				return client;
			}

			@Override
			public List<IClientConfiguration> getConfigurations() {
				return clientProvider.getRoboClients();
			}
		});

		this.appSocket = new DatagramSocket(appPort);
		this.communicationManagerApp = new CommunicationManager(new IClientManager() {

			@Override
			public List<IClientConfiguration> getConfigurations() {
				return clientProvider.getAppClients();
			}

			@Override
			public IClientConfiguration createClientConfiguration() {
				Client client = new Client();
				clientProvider.addAppClient(client);
				return client;
			}
		});

		this.roboCommunication = new Communication(communicationManagerRobo, roboSocket);
		this.roboThread = new Thread(roboCommunication);
		this.roboThread.start();

		this.appCommunication = new Communication(communicationManagerApp, appSocket);
		this.appThread = new Thread(appCommunication);
		this.appThread.start();
	}

	public void sendToRobo(Client client) {
		try {
			appCommunication.sendToClient(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		roboSocket.close();
		roboCommunication.stop();
		appCommunication.stop();
	}
}