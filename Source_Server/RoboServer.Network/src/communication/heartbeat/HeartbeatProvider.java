package communication.heartbeat;

import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatProvider implements Runnable {

	// Fields
	private final Timer timer;
	private final IHeartbeatCreator creator;
	private final long time;

	// Period of 1 seconds
	private static final long DEFAULT_TIME = 1 * 1 * 1000;

	// Constructors
	public HeartbeatProvider(IHeartbeatCreator creator) {
		this(creator, DEFAULT_TIME);
	}

	public HeartbeatProvider(IHeartbeatCreator creator, long heartbeatTime) {
		this.timer = new Timer();
		this.creator = creator;
		this.time = heartbeatTime;
	}

	// Methods
	@Override
	public void run() {
		this.timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				creator.createHeartBeat();
			}
		}, time, time);
	}

	public void stop() {
		timer.cancel();
	}
}
