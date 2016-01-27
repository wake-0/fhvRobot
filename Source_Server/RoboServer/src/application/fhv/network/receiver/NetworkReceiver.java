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
import java.net.SocketException;

public class NetworkReceiver implements INetworkReceiver {

	// Fields
	protected final DatagramSocket socket;

	// Constructor
	public NetworkReceiver(DatagramSocket socket) {
		this.socket = socket;
	}

	// Methods
	@Override
	public void receive(DatagramPacket packet) {
		try {
			if (socket.isClosed() || packet == null) {
				return;
			}

			socket.receive(packet);
		} catch (SocketException e) {
			System.out.println("Socket was closed properly.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
