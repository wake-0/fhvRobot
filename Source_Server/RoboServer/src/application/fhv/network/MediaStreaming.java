package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.sun.jna.NativeLibrary;

import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class MediaStreaming implements Runnable {

	// Fields
	private boolean isRunning;

	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;

	private final int packetSize = 64000;
	private static final String NATIVE_LIBRARY_SEARCH_PATH = "C:\\Program Files\\VideoLAN\\VLC";

	public MediaStreaming(int mediaStreamingPort) throws SocketException {
		this.socket = new DatagramSocket(mediaStreamingPort);
		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
	}

	@Override
	public void run() {
		isRunning = true;

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
		System.out.println(LibVlc.INSTANCE.libvlc_get_version());

		while (isRunning) {
			byte[] receiveData = new byte[packetSize];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			receiver.receive(receivePacket);

			if (socket.isClosed()) {
				continue;
			}

			System.out.println("length=" + receivePacket.getLength());

			String media = "udp://@:13337";
			String options = formatHttpStream("127.0.0.1", 5555);

			System.out.println("Streaming '" + media + "' to '" + options + "'");

			MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory("");
			HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
			mediaPlayer.playMedia(media, options);
		}
	}

	private static String formatHttpStream(String serverAddress, int serverPort) {
		StringBuilder sb = new StringBuilder(60);
		sb.append(":sout=#duplicate{dst=std{access=http,mux=ts,");
		sb.append("dst=");
		sb.append(serverAddress);
		sb.append(':');
		sb.append(serverPort);
		sb.append("}}");
		return sb.toString();
	}

	public void stop() {
		isRunning = false;
		socket.close();
	}
}
