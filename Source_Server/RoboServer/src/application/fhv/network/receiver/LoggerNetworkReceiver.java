package network.receiver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LoggerNetworkReceiver extends NetworkReceiver {

	public LoggerNetworkReceiver(DatagramSocket socket) {
		super(socket);
	}
	
	@Override
	public void receive(DatagramPacket packet) {
		super.receive(packet);
		
		System.out.println("Received message: " + (packet == null ? "" : new String(packet.getData())));
	}

}
