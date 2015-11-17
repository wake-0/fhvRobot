import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import communication.IClientConfiguration;
import communication.managers.CommunicationManager;
import communication.managers.IDataReceivedHandler;
import communication.managers.IAnswerHandler;
import communication.managers.IClientManager;

public class UDPClient implements Runnable, IDataReceivedHandler, IAnswerHandler, IClientManager {

	private String address = "127.0.0.1";
	//private String address = "83.212.127.13";
	private int port = 997;
	private DatagramSocket clientSocket;
	private int sessionId = 0b10000000;
	
	private CommunicationManager manager;
	private IClientConfiguration configuration;
	
	@Inject
	public UDPClient(CommunicationManager manager) {
		try {
			clientSocket = new DatagramSocket();
			this.manager = manager;
			
			this.configuration = new ClientConfiguration();
			configuration.setIpAddress(address);
			configuration.setPort(port);
			configuration.setSessionId(sessionId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			
			try {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String sentence = inFromUser.readLine();
				
				System.out.println("Send message:" + sentence);
				DatagramPacket sendPacket = manager.createDatagramPacket(configuration, sentence);
				System.out.println("Enhanced send message:" + new String(sendPacket.getData()));
				
				clientSocket.send(sendPacket);
				
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);

				System.out.println("Receive message:" + new String(receivePacket.getData()));
				manager.readDatagramPacket(receivePacket, this, this);
				
				// use new configuration because session id is set correct
				configuration = manager.getCurrentClientConfiguration();
			} catch (Exception ex) {

			}
		}
		//clientSocket.close();
	}

	@Override
	public void answer(IClientConfiguration configuration, byte[] data) {
		
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		System.out.println("Enhanced receive message:" + new String(data));
		return false;
	}


	@Override
	public IClientConfiguration createClientConfiguration() {
		return new ClientConfiguration();
	}

	@Override
	public List<IClientConfiguration> getConfigurations() {
		return new ArrayList<>();
	}
}
