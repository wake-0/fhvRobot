package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClientConfiguration;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IClientManager;
import models.Client;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.Communication;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

@Singleton
public class NetworkServer {

	// field which stores the clients
	private final IClientProvider clientProvider;

	private final CommunicationManager communicationManagerRobo;
	private final INetworkSender roboSender;
	private final INetworkReceiver roboReceiver;
	private final DatagramSocket roboSocket;
	private final Thread roboCommunication;

	private final CommunicationManager communicationManagerApp;
	private final INetworkSender appSender;
	private final INetworkReceiver appReceiver;
	private final DatagramSocket appSocket;
	private final Thread appCommunication;

	// server specific stuff
	private boolean isRunning = true;
	private final int roboPort = 997;
	private final int appPort = 998;
	private final int receivePacketSize = 1024;

	// constructors
	@Inject
	public NetworkServer(IClientProvider clientProvider) throws SocketException {
		this.clientProvider = clientProvider;

		this.roboSocket = new DatagramSocket(roboPort);
		// Added network sender and receiver which can log
		this.roboSender = new LoggerNetworkSender(roboSocket);
		this.roboReceiver = new LoggerNetworkReceiver(roboSocket);
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
		this.appSender = new LoggerNetworkSender(appSocket);
		this.appReceiver = new LoggerNetworkReceiver(appSocket);
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

		this.roboCommunication = new Thread(new Communication() {

			@Override
			public void run() {
				while (isRunning) {
					byte[] receiveData = new byte[receivePacketSize];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					roboReceiver.receive(receivePacket);

					if (roboSocket.isClosed()) {
						continue;
					}

					communicationManagerRobo.readDatagramPacket(receivePacket, this, this);
				}
			}

			@Override
			public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
				try {
					String name = new String(data);

					Client client = (Client) communicationManagerRobo.getCurrentClientConfiguration();
					client.setName(name);
					client.setReceiveData(name);

					// TODO: handle other message

					// Only for test purposes
					client.setSendData(name);
					sendToRobo(client);

				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			public void answer(IClientConfiguration configuration, byte[] data) {
				InetAddress address;
				try {
					address = InetAddress.getByName(configuration.getIpAddress());
				} catch (UnknownHostException e) {
					e.printStackTrace();
					address = InetAddress.getLoopbackAddress();
				}

				int port = configuration.getPort();
				DatagramPacket answerPacket = new DatagramPacket(data, data.length, address, port);

				roboSender.send(answerPacket);
			}
		});
		this.roboCommunication.start();

		this.appCommunication = new Thread(new Communication() {

			@Override
			public void answer(IClientConfiguration configuration, byte[] data) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void run() {
				while (isRunning) {
					byte[] receiveData = new byte[receivePacketSize];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					appReceiver.receive(receivePacket);

					if (appSocket.isClosed()) {
						continue;
					}

					communicationManagerApp.readDatagramPacket(receivePacket, this, this);
				}
			}
		});
		this.appCommunication.start();
	}

	// methods
	public void sendToRobo(Client client) throws IOException {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = communicationManagerRobo.createDatagramPacket(client, client.getSendData());
		roboSender.send(sendPacket);
	}

	public void sendToApp(Client client) throws IOException {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = communicationManagerApp.createDatagramPacket(client, client.getSendData());
		appSender.send(sendPacket);
	}

	public void shutdown() {
		roboSocket.close();
		roboCommunication.stop();
		appCommunication.stop();

		isRunning = false;
	}
}