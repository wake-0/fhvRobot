package communication.managers;

import communication.IClient;

public interface IApplicationMessageHandler {

	public void handleMessage(IClient client, String message);
	
}
