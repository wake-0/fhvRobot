/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: PresentationPDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import communication.utils.ByteParser;

public class PresentationPDU extends PDUDecorator {

	// Fields
	private byte flags = (byte) 0b00000000;

	// Constructor
	public PresentationPDU(PDU data) {
		super(data);

		header = new byte[] { flags };
	}

	public PresentationPDU(int flags, PDU data) {
		super(data);

		this.flags = ByteParser.intToByte(flags);
		header = new byte[] { this.flags };
	}

	// Methods
	public byte getFlags() {
		return flags;
	}

	@Override
	protected byte[] getEnhanceDataCore(PDU packet) {
		try {

			// Add flag bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(header);
			outputStream.write(packet.getEnhancedData());
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] getInnerDataCore(PDU packet) {
		return packet.getInnerData();
	}

}
