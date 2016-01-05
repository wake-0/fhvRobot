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

	private final long delay;
	private final long period;

	// Period of 1 minute
	// private static final long DEFAULT_TIME = 1 * 60 * 1000;
	// Period of 5 seconds
	private static final long DEFAULT_TIME = 1 * 5 * 1000;

	private final IHeartbeatHandler<T> handler;

	// Constructor
	public HeartbeatManager(T configuration, IHeartbeatHandler<T> handler) {
		this(configuration, handler, DEFAULT_TIME, DEFAULT_TIME);
	}

	public HeartbeatManager(T configuration, IHeartbeatHandler<T> handler, long delay, long period) {
		this.configuration = configuration;

		this.timer = new Timer();
		this.handler = handler;
		this.delay = delay;
		this.period = period;
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
	
	public void stopHeartbeat() {
		this.timer.cancel();
		this.timer.purge();
	}
}
