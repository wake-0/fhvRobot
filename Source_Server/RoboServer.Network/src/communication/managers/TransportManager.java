package communication.managers;

import java.net.DatagramPacket;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;

@Singleton
public class TransportManager extends LayerManager<Integer> {

	@Inject
	public TransportManager(IClientManager manager, CurrentClientService currentClientService) {
		super(manager, currentClientService);
	}

	@Override
	protected Integer getDefaultValue() {
		return -1;
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		
		IClient client = currentClientService.getClient();
		int port = packet.getPort();
		client.setPort(port);
		setValueOfClient(client, port);
		
		return false;
	}
}
