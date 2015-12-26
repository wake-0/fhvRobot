package communication.heartbeat;

import communication.configurations.IConfiguration;

/**
 * Created by Kevin on 26.12.2015.
 */
public interface IHeartbeatHandler<T extends IConfiguration> {

	void handleNoHeartbeat(T configuration);

	void handleHeartbeat(T configuration);
}
