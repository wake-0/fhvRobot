package communication.managers;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClientConfiguration;

@Singleton
public class SessionManager extends LayerManager {

	@Inject
	public SessionManager(IClientManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Fields
	private final int minSessionNumber = 0;
	private final int maxSessionNumber = 255;
	
	private final byte initConnectionFlags = (byte)0b10000000;
	private final byte initConnectionSession = (byte)0b00000000;
	
	// Methods
	private int createNewSessionNumber(int oldSessionNumber) {
		int newNumber = oldSessionNumber;
		{
			// Create new session number between min and max
			newNumber = ThreadLocalRandom.current().nextInt(minSessionNumber, maxSessionNumber + 1);
		} while(newNumber == oldSessionNumber);
		
		return newNumber;
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		byte flags = data[0];
		byte sessionId = data[1];
		boolean handled = false;
		
		IClientConfiguration currentClient = currentClientService.getClient();
		
		// TODO: add session checking for security
		
		if (flags == initConnectionFlags && sessionId == initConnectionSession) {
			
			int newSession = createNewSessionNumber(currentClient.getSessionId());
			byte[] bytes = ByteBuffer.allocate(4).putInt(newSession).array();
			byte newSessionByte = bytes[3];
			
			// TODO: update session stuff
			byte[] answer = data;
			answer[0] = (byte)0x00;
			answer[1] = newSessionByte;
			sender.answer(currentClient, answer);

			// test purpose
			currentClient.setSessionId(newSession);
			
			handled = true;
		} 
		
		return handled;
	}
	
	
}
