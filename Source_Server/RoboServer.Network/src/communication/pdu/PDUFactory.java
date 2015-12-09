package communication.pdu;

import java.util.Arrays;

import communication.utils.NumberParser;

public class PDUFactory {

	public static NetworkPDU createNetworkPDU(byte[] data) {
		if (data == null || data.length < 1) {
			return null;
		}

		int length = NumberParser.byteToUnsignedInt(data[0]);
		// Remove unused data
		byte[] newData = Arrays.copyOfRange(data, 1, 1 + length);
		return new NetworkPDU(new PDU(newData));
	}

	public static TransportPDU createTransportPDU(byte[] data) {
		if (data == null) {
			return null;
		}
		return new TransportPDU(new PDU(data));
	}

	public static SessionPDU createSessionPDU(byte[] data) {
		if (data == null || data.length < 2) {
			return null;
		}

		// TODO: check data length
		int flags = NumberParser.intToByte(data[0]);
		int sessionId = NumberParser.intToByte(data[1]);
		byte[] newData = Arrays.copyOfRange(data, 2, data.length);

		return new SessionPDU(flags, sessionId, new PDU(newData));
	}

	public static PresentationPDU createPresentationPDU(byte[] data) {
		if (data == null || data.length < 1) {
			return null;
		}

		int flags = NumberParser.intToByte(data[0]);
		byte[] newData = Arrays.copyOfRange(data, 1, data.length);

		return new PresentationPDU(flags, new PDU(newData));
	}

	public static ApplicationPDU createApplicationPDU(byte[] data) {
		if (data == null || data.length < 3) {
			return null;
		}

		int flags = NumberParser.intToByte(data[0]);
		int command = NumberParser.intToByte(data[1]);
		// int length = NumberParser.intToByte(data[2]);

		byte[] newData = Arrays.copyOfRange(data, 3, data.length);
		return new ApplicationPDU(flags, command, new PDU(newData));
	}
}
