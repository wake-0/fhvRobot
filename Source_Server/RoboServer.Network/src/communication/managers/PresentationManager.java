package communication.managers;

import java.net.DatagramPacket;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PresentationManager extends LayerManager<String>{

	@Inject
	public PresentationManager(IClientManager manager, CurrentClientService currentClientService) {
		super(manager, currentClientService);
	}

	@Override
	protected String getDefaultValue() {
		return "";
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		return false;
	}
}
