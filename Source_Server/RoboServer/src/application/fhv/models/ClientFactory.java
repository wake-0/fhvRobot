package models;

public class ClientFactory {

	public static Client createClient(String ipAddress, int port) {
		Client client = new Client();
		client.setIpAddress(ipAddress);
		client.setPort(port);
		return client;
	}
	
}
