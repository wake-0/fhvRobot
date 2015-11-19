package communication.managers;

import communication.IClientConfiguration;

public interface IAnswerHandler {

	public void answer(IClientConfiguration configuration, byte[] data);

}
