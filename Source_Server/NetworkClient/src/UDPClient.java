import java.io.*;
import java.net.*;

public class UDPClient {
	public static void main(String[] args) {
		try {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			DatagramSocket clientSocket = new DatagramSocket();
			String address = "127.0.0.1";
			//String address = "83.212.127.13";
			InetAddress IPAddress = InetAddress.getByName(address);
			//int port = 997;
			int port = 8632;
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			String sentence = inFromUser.readLine();
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
			clientSocket.close();
		} catch (Exception ex) {

		}
	}
}