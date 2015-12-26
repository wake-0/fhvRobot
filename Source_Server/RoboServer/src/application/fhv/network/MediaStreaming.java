package network;

import java.net.DatagramSocket;
import java.net.SocketException;

import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

public class MediaStreaming implements Runnable {

	// Fields
	private boolean isRunning;

	protected final INetworkReceiver receiver;
	protected final INetworkSender sender;
	protected final DatagramSocket socket;

	public MediaStreaming(int mediaStreamingPort) throws SocketException {
		this.socket = new DatagramSocket(mediaStreamingPort);
		this.receiver = new LoggerNetworkReceiver(socket);
		this.sender = new LoggerNetworkSender(socket);
	}

	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			String media = "udp://@:13337";
			String options = formatHttpStream("127.0.0.1", 990);

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
