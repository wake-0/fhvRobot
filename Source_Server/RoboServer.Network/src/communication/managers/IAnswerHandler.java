/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: IAnswerHandler.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import communication.IConfiguration;

public interface IAnswerHandler {

	public void answer(IConfiguration configuration, byte[] data);
}
