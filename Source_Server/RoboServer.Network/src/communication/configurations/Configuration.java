package communication.configurations;

import java.net.SocketAddress;

public class Configuration implements IConfiguration {

	// Fields
	private int sessionId;
	private int port;
	private String ipAddress;
	private int heartBeatCount;
	private SocketAddress socketAddress;

	// Constructor
	public Configuration(int sessionId, int port, String ipAddress) {
		this.sessionId = sessionId;
		this.port = port;
		this.ipAddress = ipAddress;
		this.heartBeatCount = 0;
	}

	// Methods
	@Override
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public int getSessionId() {
		return sessionId;
	}

	@Override
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public int getHeartBeatCount() {
		return heartBeatCount;
	}

	@Override
	public void increaseHeartBeatCount() {
		heartBeatCount++;
	}

	@Override
	public void cleanHeartBeatCount() {
		heartBeatCount = 0;
	}

	@Override
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
}
