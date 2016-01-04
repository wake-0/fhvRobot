package app.robo.fhv.roboapp.communication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class MediaStreaming implements Runnable {

	private static final String LOG_TAG = "MediaStreaming";
	private final IFrameReceived callback;

	public interface IFrameReceived {
		void frameReceived(Bitmap i);
	}

	private static final int MAX_PACKET_SIZE	=	0xFFFF;
	private static final int MAX_FRAME_SIZE		=	0xFFFFF;

	private boolean isRunning;

	protected final DatagramSocket socket;
	
	private ByteBuffer imageBuffer;

	public MediaStreaming(int mediaStreamingPort, IFrameReceived callback) throws SocketException {
		this.socket = new DatagramSocket(mediaStreamingPort);
		imageBuffer = ByteBuffer.allocate(MAX_FRAME_SIZE);
		this.callback = callback;
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				byte[] receiveData = new byte[MAX_PACKET_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);

				if (socket.isClosed()) {
					continue;
				}

				byte[] data = receivePacket.getData();
				int len = receivePacket.getLength();

				Log.d(LOG_TAG, "Got frame package with len=" + len);

				if (findMPEGStart(data, len) > 0) {
					imageBuffer.clear();
					imageBuffer.put(receivePacket.getData(), 0, receivePacket.getLength());
				} else {
					imageBuffer.put(receivePacket.getData(), 0, receivePacket.getLength());
				}
				if (isMPEGEndPacket(data, len)) {
					final Bitmap i = deserializeImage(imageBuffer.array());
					callback.frameReceived(i);
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, "Dropped frame!");
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
	
	private Bitmap deserializeImage(byte[] imageData) {
		Bitmap bitmap;
		bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
		return bitmap;
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}
}