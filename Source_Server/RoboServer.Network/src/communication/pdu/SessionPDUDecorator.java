package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SessionPDUDecorator extends PDUDecorator {

	private byte[] flags = new byte[] { 0b00000000 };
	private byte[] sessionId = new byte[] { 0b00000000 };
	
	public SessionPDUDecorator(PDU data) {
		super(data);
	}
	
	public SessionPDUDecorator(int sessionId, PDU data) {
		super(data);
		
		// Integer to byte array
		byte[] bytes = ByteBuffer.allocate(4).putInt(sessionId).array();
		this.sessionId = new byte[] { bytes[3] };
	}

	@Override
	protected byte[] enhanceData(PDU packet) {
		try {
			
			// Add flag and session bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(flags);
			outputStream.write(sessionId);
			outputStream.write(packet.getEnhancedData());
			return outputStream.toByteArray();
			
		} catch (IOException e) {
			return data;
		}
	}
	
	@Override
	protected byte[] innerData(PDU packet) {
		// Remove the flag and session bytes
		byte[] data = packet.getInnerData();
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
