package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.PDU;
import communication.utils.NumberParser;

public class DatagramFactory {

	public static DatagramPacket createNoFreeSlotPacket(IConfiguration configuration) {
		byte answerFlags = NumberParser.intToByte(ConfigurationSettings.NO_FREE_SESSION_FLAGS);
		byte answerSessionId = NumberParser.intToByte(ConfigurationSettings.DEFAULT_SESSION_ID);
		byte[] answer = new byte[] { answerFlags, answerSessionId };
		return createPacket(configuration, answer);
	}

	public static DatagramPacket createDisconnectedPacket(IConfiguration configuration) {
		byte answerFlags = NumberParser.intToByte(ConfigurationSettings.DISCONNECTED);
		byte answerSessionId = NumberParser.intToByte(configuration != null ? configuration.getSessionId() : 0);
		byte[] answer = new byte[] { answerFlags, answerSessionId };
		return createPacket(configuration, answer);
	}

	public static DatagramPacket createSessionPacket(IConfiguration configuration, int sessionId) {
		byte answerFlags = NumberParser.intToByte(ConfigurationSettings.REQUEST_SESSION_FLAGS);
		byte answerSessionId = NumberParser.intToByte(sessionId);
		byte[] answer = new byte[] { answerFlags, answerSessionId };
		return DatagramFactory.createPacket(configuration, answer);
	}

	public static DatagramPacket createPacketFromPDU(IConfiguration configuration, PDU pdu) {
		byte[] data = pdu.getEnhancedData();
		int length = data.length;
		InetAddress ipAddress;

		try {
			ipAddress = InetAddress.getByName(configuration.getIpAddress());
		} catch (UnknownHostException e) {
			ipAddress = InetAddress.getLoopbackAddress();
		}

		return new DatagramPacket(data, length, ipAddress, configuration.getPort());
	}

	private static DatagramPacket createPacket(IConfiguration configuration, byte[] data) {
		InetAddress address = parseStringToInetAddress(configuration.getIpAddress());
		int port = configuration.getPort();
		return new DatagramPacket(data, data.length, address, port);
	}

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
