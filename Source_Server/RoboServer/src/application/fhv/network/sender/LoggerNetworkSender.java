/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: LoggerNetworkSender.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network.sender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LoggerNetworkSender extends NetworkSender {

	// Constructor
	public LoggerNetworkSender(DatagramSocket socket) {
		super(socket);
	}

	// Methods
	@Override
	public void send(DatagramPacket packet) {
		super.send(packet);

		System.out.println("Send message: " + (packet == null ? "" : new String(packet.getData())));
	}
}