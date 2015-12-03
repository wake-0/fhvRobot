/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: NetworkReceiver.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package network.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NetworkReceiver implements INetworkReceiver {

	private final DatagramSocket socket;

	public NetworkReceiver(DatagramSocket socket) {
		this.socket = socket;
	}

	@Override
	public void receive(DatagramPacket packet) {
		try {
			if (socket.isClosed() || packet == null) {
				return;
			}

			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
