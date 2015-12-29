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

import communication.configurations.ConfigurationSettings;
import communication.utils.NumberParser;

public class ApplicationPDU extends PDUDecorator {

	// Fields
	private byte flags;
	private byte command;
	private byte length;

	// Constructors
	public ApplicationPDU(PDU data) {
		this(ConfigurationSettings.DEFAULT_APPLICATION_FLAGS, ConfigurationSettings.DEFAULT_APPLICATION_COMMAND, data);
	}

	public ApplicationPDU(int flags, int command, PDU pdu) {
		super(pdu);

		this.flags = NumberParser.intToByte(flags);
		this.command = NumberParser.intToByte(command);
		this.length = NumberParser.intToByte(pdu.getData().length);

		header = new byte[] { this.flags, this.command, this.length };
	}

	// Methods
	public byte getFlags() {
		return flags;
	}

	public int getCommand() {
		return command;
	}

	public int getPayloadLength() {
		return length;
	}

	public byte[] getPayload() {
		return data;
	}

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
		return packet.getInnerData();
	}
}
