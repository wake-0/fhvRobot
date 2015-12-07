/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: IConfiguration.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.configurations;

public interface IConfiguration {

	public void setSessionId(int sessionId);

	public int getSessionId();

	public void setIpAddress(String ipAddress);

	public String getIpAddress();

	public void setPort(int port);

	public int getPort();

	public int getHeartBeatCount();

	public void increaseHeartBeatCount();

	public void cleanHeartBeatCount();
}
