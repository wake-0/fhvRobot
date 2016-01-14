package communication.pdu;

import java.util.Arrays;

import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.utils.NumberParser;

public class PDUFactory {

	public static PDU createOpenConnectionPDU() {
		int sessionId = ConfigurationSettings.DEFAULT_SESSION_ID;
		int flags = ConfigurationSettings.REQUEST_SESSION_FLAGS;
		String openMessage = ConfigurationSettings.OPEN_MESSAGE;

		return new NetworkPDU(new TransportPDU(
				new SessionPDU(flags, sessionId, new PresentationPDU(new ApplicationPDU(new PDU(openMessage))))));
	}

	public static PDU createHeartbeatPDU(IConfiguration configuration) {
		int flag = ConfigurationSettings.DEFAULT_APPLICATION_FLAGS;
		int command = ConfigurationSettings.DEFAULT_APPLICATION_COMMAND;
		byte[] payload = new byte[] { 0 };

		return createApplicationPDU(configuration, flag, command, payload);
	}

	public static PDU createApplicationPDU(IConfiguration configuration, int flag, int command, byte[] payload) {
		return new NetworkPDU(new TransportPDU(new SessionPDU(configuration.getSessionId(),
				new PresentationPDU(new ApplicationPDU(flag, command, new PDU(payload))))));
	}

	public static NetworkPDU createNetworkPDU(byte[] data) {
		return new NetworkPDU(new PDU(data));
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
		//int length = NumberParser.intToByte(data[2]);
		
		byte[] newData;
		
		int extendedLength = flags & 0x02;
		
		if(extendedLength == 2) {
			newData = Arrays.copyOfRange(data, 4, data.length);
		} else {
			newData = Arrays.copyOfRange(data, 3, data.length);
		}
		
		//byte[] newData = Arrays.copyOfRange(data, 3, data.length);
		return new ApplicationPDU(flags, command, new PDU(newData));
	}
}
