package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import javafx.application.Platform;
import javafx.scene.image.Image;
import models.Client;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

public class MediaStreaming implements Runnable {
	
	public interface IMediaStreamingFrameReceived {
		public void frameReceived(Image image, Client client);
	}
	
	private static final int MAX_PACKET_SIZE	=	0xFFFF;
	private static final int MAX_FRAME_SIZE		=	0xFFFFF;

	// Fields
	private boolean isRunning;

	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;
	
	private ByteBuffer imageBuffer;
	private Client client;
	private IMediaStreamingFrameReceived callback;

	public MediaStreaming(int mediaStreamingPort, Client client, MediaStreaming.IMediaStreamingFrameReceived callback) throws SocketException {
		this.socket = new DatagramSocket(mediaStreamingPort);
		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
		this.client = client;
		this.callback = callback;
		imageBuffer = ByteBuffer.allocate(MAX_FRAME_SIZE);
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {

			byte[] receiveData = new byte[MAX_PACKET_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			receiver.receive(receivePacket);

			if (socket.isClosed()) {
				continue;
			}

			byte[] data = receivePacket.getData();
			int len = receivePacket.getLength();
			
			if (findMPEGStart(data, len) > 0) {
				imageBuffer.clear();
				imageBuffer.put(receivePacket.getData(), 0, receivePacket.getLength());
			} else {
				imageBuffer.put(receivePacket.getData(), 0, receivePacket.getLength());
			}
			if (isMPEGEndPacket(data, len)) {
				try {
					final Image i = deserializeImage(imageBuffer.array());
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							callback.frameReceived(i, client);
						}
					});
				} catch (IOException e) {
					System.out.println("WARNING: Dropped frame!");
				}
			}
			
		}
	}
	
	private boolean isMPEGEndPacket(byte[] data, int len) {
		if (data[len - 2] == (byte)0xFF && data[len - 1] == (byte)0xD9) {
			return true;
		}
		return false;
	}
	
	private int findMPEGStart(byte[] data, int len) {
		byte[] huf = new byte[] { (byte) 0xff, (byte) 0xc4, 0x00, 0x1f, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01 };
		for (int i = 0; i < data.length; i++) {
			boolean found = true;
			for (int j = 0; j < huf.length && found == true; j++) {
				if (data[i + j] != huf[j]) {
					found = false;
				}
			}
			if (found == true) return i;
		}
		return -1;
	}
	
	private Image deserializeImage(byte[] imageData) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(imageData);
		return new Image(in);
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}
}
