package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import models.Client;

public class NetworkServer implements Runnable {

	private Client client;
	private int port = 997;
	private ServerSocket serverSocket;
	
	private List<NetworkConnection> connections;
	private Socket clientSocket;
	
	public NetworkServer(Client client) {
		this.client = client;
		this.connections = new ArrayList<>();
		
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			test();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void test() throws IOException {
		//while(true) {
			clientSocket = warteAufAnmeldung(serverSocket);
			NetworkConnection connection = new NetworkConnection(clientSocket, this.client);
			connections.add(connection);
			
			while(true) {
				this.client.setName(leseNachricht(clientSocket));
			}
			
			
			//schreibeNachricht(client, this.client.getData());
		//}
	}

	Socket warteAufAnmeldung(java.net.ServerSocket serverSocket) throws IOException {
		Socket socket = serverSocket.accept(); 
		return socket;
	}

	String leseNachricht(java.net.Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert
																	// bis
																	// Nachricht
																	// empfangen
		String nachricht = new String(buffer, 0, anzahlZeichen);
		return nachricht;
	}

	public void Send() {
		try {
			schreibeNachricht(clientSocket, client.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void schreibeNachricht(java.net.Socket socket, String nachricht) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}

	

}
