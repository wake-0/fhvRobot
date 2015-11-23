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
import java.util.Arrays;

import communication.utils.ByteParser;

public class PresentationPDUDecorator extends PDUDecorator {

	// Fields
	private byte flags = (byte) 0b00000000;

	// Constructor
	public PresentationPDUDecorator(PDU data) {
		super(data);

		header = new byte[] { flags };
	}

	public PresentationPDUDecorator(byte[] data) {
		super(data);

		header = new byte[] { flags };
	}

	public PresentationPDUDecorator(PDU data, int flags) {
		super(data);

		this.flags = ByteParser.intToByte(flags);
		header = new byte[] { this.flags };
	}

	// Methods
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
		// Remove the flag bytes
		byte[] data = packet.getInnerData();
		return Arrays.copyOfRange(data, header.length, data.length);
	}

}
