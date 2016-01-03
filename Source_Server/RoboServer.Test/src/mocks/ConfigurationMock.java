/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Test
 * Filename: ConfigurationMock.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package mocks;

import java.net.SocketAddress;

import communication.configurations.IConfiguration;

public class ConfigurationMock implements IConfiguration {

	private int sessionId;
	private int port;
	private int heartBeatCount;
	private String ipAddress;
	private SocketAddress socketAddress;

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
