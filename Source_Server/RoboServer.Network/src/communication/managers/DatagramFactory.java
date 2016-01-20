package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.PDU;
import communication.pdu.PDUFactory;
import communication.utils.NumberParser;

public class DatagramFactory {

	public static DatagramPacket createOpenConnectionDatagramPacket(IConfiguration configuration) {
		PDU pdu = PDUFactory.createOpenConnectionPDU();
		return createPacketFromPDU(configuration, pdu);
	}

	public static DatagramPacket createHeartbeatDatagramPacket(IConfiguration configuration) {
		PDU pdu = PDUFactory.createHeartbeatPDU(configuration);
		return createPacketFromPDU(configuration, pdu);
	}

	public static DatagramPacket createDatagramPacket(IConfiguration configuration, int flag, int command,
			byte[] payload) {
		PDU pdu = PDUFactory.createApplicationPDU(configuration, flag, command, payload);
		return createPacketFromPDU(configuration, pdu);
	}

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
			if (configuration.getSocketAddress() != null) {
				return new DatagramPacket(data, length, configuration.getSocketAddress());
			}

			ipAddress = InetAddress.getByName(configuration.getIpAddress());
		} catch (UnknownHostException | SocketException e) {
			ipAddress = InetAddress.getLoopbackAddress();
		}

		return new DatagramPacket(data, length, ipAddress, configuration.getPort());
	}

	public static DatagramPacket createRawBytePacket(IConfiguration configuration, byte[] data, int len) {
		return createPacket(configuration, data, len);
	}

	private static DatagramPacket createPacket(IConfiguration configuration, byte[] data) {
		return createPacket(configuration, data, data.length);
	}

	private static DatagramPacket createPacket(IConfiguration configuration, byte[] data, int len) {
		if (configuration.getSocketAddress() != null) {
			try {
				return new DatagramPacket(data, len, configuration.getSocketAddress());
			} catch (SocketException e) {
				// TODO: Check this case
				return null;
			}
		} else {
			InetAddress address = parseStringToInetAddress(configuration.getIpAddress());
			int port = configuration.getPort();
			return new DatagramPacket(data, len, address, port);
		}
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
