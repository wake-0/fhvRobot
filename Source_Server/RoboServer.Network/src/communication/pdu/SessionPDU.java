/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: SessionPDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import communication.utils.NumberParser;

public class SessionPDU extends PDUDecorator {

	// Fields
	private byte flags = (byte) 0b00000000;
	private byte sessionId = (byte) 0b00000000;

	// Constructors
	public SessionPDU(PDU data) {
		super(data);
		header = new byte[] { this.flags, this.sessionId };
	}

	public SessionPDU(int sessionId, PDU data) {
		super(data);

		this.sessionId = NumberParser.intToByte(sessionId);
		header = new byte[] { this.flags, this.sessionId };
	}

	public SessionPDU(int flags, int sessionId, PDU data) {
		super(data);

		this.flags = NumberParser.intToByte(flags);
		this.sessionId = NumberParser.intToByte(sessionId);
		header = new byte[] { this.flags, this.sessionId };
	}

	// Methods
	@Override
	protected byte[] getEnhanceDataCore(PDU packet) {
		try {
			// Add flag and session bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(header);
			outputStream.write(packet.getEnhancedData());
			System.out.println(outputStream.toByteArray());
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] getInnerDataCore(PDU packet) {
		// Remove the flag and session bytes
		byte[] data = packet.getInnerData();
		return Arrays.copyOfRange(data, header.length, data.length);
	}

	public byte getFlags() {
		return flags;
	}

	public int getSessionId() {
		return sessionId;
	}
}
