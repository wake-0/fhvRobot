/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: ApplicationPDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ApplicationPDUDecorator extends PDUDecorator {

	// Fields
	private byte flags = (byte) 0b00000000;
	private byte commands = (byte) 0b00000000;
	private byte length = (byte) 0b00000000;

	// Constructor
	public ApplicationPDUDecorator(PDU data) {
		super(data);

		header = new byte[] { flags, commands, length };
	}

	public ApplicationPDUDecorator(byte[] data) {
		super(data);

		header = new byte[] { flags, commands, length };
	}

	// Methods
	@Override
	protected byte[] getEnhanceDataCore(PDU packet) {
		try {

			// Add flag bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(header);
			outputStream.write(data);
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] getInnerDataCore(PDU packet) {
		return Arrays.copyOfRange(packet.getInnerData(), header.length, data.length);
	}
}
