package communication.heartbeat;

import java.util.Timer;
import java.util.TimerTask;

import communication.configurations.IConfiguration;

/**
 * Created by Kevin on 26.12.2015.
 */
public class HeartbeatManager<T extends IConfiguration> implements Runnable {

	// Fields
	private final T configuration;

	private final Timer timer;
	// Period of 1 minute
	// private final long delay = 1 * 60 * 1000;
	// private final long period = 1 * 60 * 1000;

	// Period of 5 seconds
	private final long delay = 1 * 5 * 1000;
	private final long period = 1 * 5 * 1000;

	private final IHeartbeatHandler<T> handler;

	// Constructor
	public HeartbeatManager(T configuration, IHeartbeatHandler<T> handler) {
		this.configuration = configuration;

		this.timer = new Timer();
		this.handler = handler;
	}

	// Methods
	@Override
	public void run() {
		this.timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (configuration.getHeartBeatCount() == 0) {
					timer.cancel();
					handler.handleNoHeartbeat(configuration);
				} else {
					handler.handleHeartbeat(configuration);
				}
			}
		}, delay, period);
	}
}
