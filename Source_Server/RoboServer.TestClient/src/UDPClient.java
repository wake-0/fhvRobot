import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.google.inject.Inject;

import communication.IClient;
import communication.managers.CommunicationManager;
import communication.managers.IDataReceivedHandler;
import communication.managers.IAnswerHandler;
import communication.managers.IClientManager;

public class UDPClient implements Runnable, IClient, IDataReceivedHandler, IAnswerHandler, IClientManager {

	private String address = "127.0.0.1";
	//private String address = "83.212.127.13";
	private int port = 997;
	private DatagramSocket clientSocket;
	private int sessionId = 0b10000000;
	
	private CommunicationManager manager;
	
	@Inject
	public UDPClient(CommunicationManager manager) {
		try {
			clientSocket = new DatagramSocket();
			
			this.manager = manager;
			manager.addClient(this);
			manager.setIpAddress(this, address);
			manager.setPort(this, port);
			manager.setSessionId(this, sessionId);
			
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
				DatagramPacket sendPacket = manager.createDatagramPacket(this, sentence);
				System.out.println("Enhanced send message:" + new String(sendPacket.getData()));
				
				clientSocket.send(sendPacket);
				
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);

				System.out.println("Receive message:" + new String(receivePacket.getData()));
				manager.readDatagramPacket(receivePacket, this, this);
								
			} catch (Exception ex) {

			}
		}
		//clientSocket.close();
	}

	@Override
	public void answer(byte[] data) {
		
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		System.out.println("Enhanced receive message:" + new String(data));
		return false;
	}

	@Override
	public void setSessionId(int sessionId) {
		System.out.println("new session:" + sessionId);
	}

	@Override
	public void setIpAddress(String ipAddress) {
	}

	@Override
	public void setPort(int port) {
	}

	@Override
	public IClient createClient() {
		return new IClient() {
			
			@Override
			public void setSessionId(int sessionId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPort(int port) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setIpAddress(String ipAddress) {
				// TODO Auto-generated method stub
				
			}
		};
	}
}
