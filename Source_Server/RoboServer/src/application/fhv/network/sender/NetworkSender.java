/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: NetworkSender.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NetworkSender implements INetworkSender {

	// Fields
	protected final DatagramSocket socket;

	// Constructor
	public NetworkSender(DatagramSocket socket) {
		this.socket = socket;
	}

	// Methods
	@Override
	public void send(DatagramPacket packet) {
		try {
			if (socket.isClosed() || packet == null) {
				return;
			}

			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
