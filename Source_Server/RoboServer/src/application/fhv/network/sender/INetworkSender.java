/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: INetworkSender.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network.sender;

import java.net.DatagramPacket;

public interface INetworkSender {

	void send(DatagramPacket packet);

}