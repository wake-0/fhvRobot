package communication.managers;

import java.net.DatagramPacket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;

@Singleton
public class SessionManager extends LayerManager<Integer> {

	@Inject
	public SessionManager(IClientManager manager, CurrentClientService currentClientService) {
		super(manager, currentClientService);
	}

	// Fields
	private final int minSessionNumber = 0;
	private final int maxSessionNumber = 255;
	
	private final byte initConnectionFlags = (byte)0b10000000;
	private final byte initConnectionSession = (byte)0b00000000;
	
	// Methods
	public int getSession(IClient client) {
		return clientMap.get(client);
	}
	
	public int createSession(IClient client) {
		addClient(client);
		
		// Set new session id
		int oldSession = getValue(client);
		int newSession = createNewSessionNumber(oldSession);
		setValueOfClient(client, newSession);
		
		return newSession;
	}
	
	private int createNewSessionNumber(int oldSessionNumber) {
		int newNumber = oldSessionNumber;
		{
			// Create new session number between min and max
			newNumber = ThreadLocalRandom.current().nextInt(minSessionNumber, maxSessionNumber + 1);
		} while(newNumber == oldSessionNumber);
		
		return newNumber;
	}

	@Override
	protected Integer getDefaultValue() {
		return -1;
	}
	
	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		byte flags = data[0];
		byte sessionId = data[1];
		boolean handled = false;
		
		IClient currentClient = currentClientService.getClient();
		
		if (flags == initConnectionFlags && sessionId == initConnectionSession) {
			
			// TODO: update session stuff
			byte[] test = new byte[]{ 0b01010101 };
			sender.answer(test);

			// test purpose
			currentClient.setSessionId(7);
			clientMap.put(currentClient, 7);
			
			handled = true;
		} 
		
		
		// TODO: return true when session connect called
		return handled;
	}
	
	
}
