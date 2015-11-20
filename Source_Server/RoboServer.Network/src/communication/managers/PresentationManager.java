package communication.managers;

import java.net.DatagramPacket;

import com.google.inject.Inject;
import com.google.inject.Singleton;

public class PresentationManager extends LayerManager {

	public PresentationManager(IClientManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		return false;
	}
}
