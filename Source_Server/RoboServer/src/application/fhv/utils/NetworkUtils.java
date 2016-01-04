package utils;

import java.net.DatagramPacket;

public class NetworkUtils {
	
	public static void printDatagramPacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		int len = packet.getLength();
		System.out.print("HEX: ");
		for (int i = 0; i < len; i++) {
			String s = Integer.toHexString(data[i] & 0xFF);
			if (s.length() == 1) s = "0" + s;
			System.out.print(s + " ");
		}
		System.out.println("");
		
		System.out.print("RAW: ");
		for (int i = 0; i < len; i++) {
			System.out.print("" + (char)(data[i]));
		}
		System.out.println("");
	}

}
