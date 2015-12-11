package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetParser {

	public static InetAddress parseStringToInetAddress(String address) {
		InetAddress returnAddress;
		try {
			returnAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			returnAddress = InetAddress.getLoopbackAddress();
		}

		return returnAddress;
	}

}
