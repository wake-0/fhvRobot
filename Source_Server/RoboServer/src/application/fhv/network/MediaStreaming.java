package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

public class MediaStreaming implements Runnable {

	// Fields
	private boolean isRunning;

	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;

	private final int packetSize = 64000;

	public MediaStreaming(int mediaStreamingPort) throws SocketException {
		this.socket = new DatagramSocket(mediaStreamingPort);
		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
	}

	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			byte[] receiveData = new byte[packetSize];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			receiver.receive(receivePacket);

			if (socket.isClosed()) {
				continue;
			}

			System.out.println("length=" + receivePacket.getLength());
			// sender.send(receivePacket);
		}
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}
}
