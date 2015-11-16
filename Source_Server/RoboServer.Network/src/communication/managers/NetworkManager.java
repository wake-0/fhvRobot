package communication.managers;

import java.net.DatagramPacket;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;

@Singleton
public class NetworkManager extends LayerManager<String> {

	@Inject
	public NetworkManager(IClientManager manager, CurrentClientService currentClientService) {
		super(manager, currentClientService);
	}

	@Override
	protected String getDefaultValue() {
		return "127.0.0.1";
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		String ipAddress = packet.getAddress().getHostName();
		
		IClient client = getClientByValue(clientMap, ipAddress);
		currentClientService.setClient(client);
		
		client.setIpAddress(ipAddress);
		setValueOfClient(client, ipAddress);
		return false;
	}
}
