package network.sender;

import java.net.DatagramPacket;

public interface INetworkSender {

	void send(DatagramPacket packet);

}