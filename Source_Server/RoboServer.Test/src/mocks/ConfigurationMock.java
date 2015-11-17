package mocks;

import communication.IClientConfiguration;

public class ConfigurationMock implements IClientConfiguration{

	private int sessionId;
	private int port;
	private String ipAddress;
	
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

}
