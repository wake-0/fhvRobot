/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: INetworkReceiver.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network.receiver;

import java.net.DatagramPacket;

public interface INetworkReceiver {

	void receive(DatagramPacket packet);

}