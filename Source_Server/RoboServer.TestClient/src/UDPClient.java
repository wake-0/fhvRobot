import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.inject.Inject;

import communication.IClient;
import communication.managers.CommunicationManager;
import communication.pdu.PDU;

public class UDPClient implements Runnable, IClient {

	private String address = "127.0.0.1";
	//private String address = "83.212.127.13";
	private InetAddress IPAddress;
	private int port = 997;
	private DatagramSocket clientSocket;
	
	private CommunicationManager manager;
	
	@Inject
	public UDPClient(CommunicationManager manager) {
		try {
			IPAddress = InetAddress.getByName(address);
			clientSocket = new DatagramSocket();
			
			this.manager = manager;
			manager.addClient(this);
			manager.setIpAddress(this, IPAddress);
			manager.setPort(this, port);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			byte[] receiveData = new byte[1024];
			
			try {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				String sentence = inFromUser.readLine();
				
				System.out.println("Send message:" + sentence);
				DatagramPacket sendPacket = manager.createDatagramPacket(this, sentence);
				System.out.println("Enhanced send message:" + new String(sendPacket.getData()));
				
				clientSocket.send(sendPacket);
				
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);

				String modifiedSentence = new String(receivePacket.getData());
				
				System.out.println("Receive message:" + modifiedSentence);
				PDU pdu = manager.createPDU(this, modifiedSentence);
				System.out.println("Enhanced receive message:" + new String(pdu.getInnerData()));
				
			} catch (Exception ex) {

			}
		}
		//clientSocket.close();
	}
}
