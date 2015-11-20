package communication.managers;

import java.net.DatagramPacket;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClientConfiguration;

public class TransportManager extends LayerManager {

	public TransportManager(IClientManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		
		IClientConfiguration client = currentClientService.getClient();
		int port = packet.getPort();
		client.setPort(port);
		
		return false;
	}
}
