package network.receiver;

import java.net.DatagramPacket;

public interface INetworkReceiver {

	void receive(DatagramPacket packet);

}