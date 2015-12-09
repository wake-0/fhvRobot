/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: NetworkPDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import communication.utils.NumberParser;

public class NetworkPDU extends PDUDecorator {

	// Fields
	private byte length;

	// Constructors
	public NetworkPDU(PDU pdu) {
		super(pdu);

		this.length = NumberParser.intToByte(pdu.getEnhancedData().length);
		header = new byte[] { this.length };
	}

	// Methods
	@Override
	protected byte[] getEnhanceDataCore(PDU pdu) {
		try {

			// Add length byte
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(header);
			outputStream.write(pdu.getEnhancedData());
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] getInnerDataCore(PDU packet) {
		// Remove the length byte
		byte[] data = packet.getInnerData();
		return Arrays.copyOfRange(data, header.length, data.length);
	}

	public int getLength() {
		return length;
	}
}
