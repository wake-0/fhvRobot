
/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.TestClient
 * Filename: ClientConfiguration.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */

import communication.IConfiguration;

public class ClientConfiguration implements IConfiguration {

	private int sessionId;
	private int port;
	private int heartBeatCount;
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

}
