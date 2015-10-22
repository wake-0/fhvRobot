package network;

import java.io.*;
import java.net.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import models.Client;

public class NetworkClient implements Runnable {
	
	private Client client;
	
	DatagramSocket socket = null;
	DatagramPacket packetOut = null;
	DatagramPacket packetIn = null;
	BufferedReader readFromKeyBoard = null;
	byte[] dataIn;
	byte[] dataOut;

	InetAddress inetAddress;
	
	public NetworkClient(Client client) {
		
		this.client = client;
		
		try {
			inetAddress = InetAddress.getLocalHost();
			
			readFromKeyBoard = new BufferedReader(new InputStreamReader(System.in));
			dataIn = new byte[1024];
			dataOut = new byte[1024];
			socket = new DatagramSocket(999);

			//new Thread(this).start();

//			
//			while (true) {
//				String message = readFromKeyBoard.readLine();
//				dataIn = message.getBytes();
//				packetOut = new DatagramPacket(dataIn, message.length(), inetAddress, 997);
//				socket.send(packetOut);
//			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				packetIn = new DatagramPacket(dataOut, dataOut.length);
				socket.receive(packetIn);
				String messageToReceive = new String(packetIn.getData(), 0, packetIn.getLength());
				client.setName(messageToReceive);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	public void Send() {
		String messageToSend = client.getData();
		if (messageToSend != null || messageToSend != "") {
			dataIn = messageToSend.getBytes();
			packetOut = new DatagramPacket(dataIn, messageToSend.length(), inetAddress, 997);
			try {
				socket.send(packetOut);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
