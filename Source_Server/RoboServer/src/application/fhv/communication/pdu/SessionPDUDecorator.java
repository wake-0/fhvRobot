package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SessionPDUDecorator extends PDUDecorator {

	private byte[] flags;
	private byte[] sessionId;
	
	public SessionPDUDecorator(PDU data) {
		super(data);
		
		flags = new byte[] { 0b00000000 };
		sessionId = new byte[] { 0b00000000 };
	}

	@Override
	protected byte[] enhanceData(byte[] data) {
		try {
			
			// Add flag and session bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(flags);
			outputStream.write(sessionId);
			outputStream.write(data);
			return outputStream.toByteArray();
			
		} catch (IOException e) {
			return data;
		}
	}
	
	@Override
	protected byte[] innerData(byte[] data) {
		// Remove the flag and session bytes
		return Arrays.copyOfRange(data, flags.length + sessionId.length , data.length);
	}

	public void setFlags(byte[] flags) {
		this.flags = flags;
	}
	
	public void setSessionId(byte[] sessionId) {
		this.sessionId = sessionId;
	}
	
	public byte[] getFlags() {
		return flags;
	}
	
	public byte[] getSessionId() {
		return sessionId;
	}
}
