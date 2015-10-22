package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import models.Client;

public class NetworkConnection implements Runnable, Comparable<NetworkConnection> {

	// fields
	private Socket socket;
	private Client client;

	// constructor
	public NetworkConnection(Socket socket, Client client) {
		this.socket = socket;
		this.client = client;
	}

	// methods
	public Client getClient() {
		return client;
	}

	@Override
	public void run() {
		while(true) {
			receive();
		}
	}

	private void receive() {
		BufferedReader bufferedReader;
		try {
			if (socket.isInputShutdown()) { return; }
			
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			char[] buffer = new char[200];
			int numberOfSigns = bufferedReader.read(buffer, 0, 200);
			client.setReceiveData(new String(buffer, 0, numberOfSigns));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send() {
		try {
			
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			printWriter.print(client.getSendData());
			printWriter.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void kill() {
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int compareTo(NetworkConnection o) {
		if (o == null) {return -1;}
		return client.compareTo(o.client);
	}
}
