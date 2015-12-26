package communication.managers;

import java.net.DatagramPacket;

import communication.configurations.Configuration;
import communication.configurations.ConfigurationFactory;
import communication.pdu.PDU;

public class ReconnectionService {

	// Fields
	private final CommunicationManager manager;

	// Constructor
	public ReconnectionService(CommunicationManager manager) {
		this.manager = manager;
	}

	// Methods
	public boolean handleDataReceived(DatagramPacket packet, PDU pdu, IAnswerHandler sender) {

		byte[] data = pdu.getData();
		byte[] requestData = new byte[] { 0, 0 };

		if (data == requestData) {
			// Create request
			Configuration configuration = ConfigurationFactory.createConfiguration(packet);
			DatagramPacket answer = manager.createOpenConnectionDatagramPacket(configuration);
			sender.answer(answer);
			return true;
		}

		return false;
	}

}
