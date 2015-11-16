package communication.managers;

import java.net.DatagramPacket;

public interface IDataReceivedHandler {

	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender);
	
}
