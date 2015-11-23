/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: PDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

public abstract class PDUDecorator extends PDU {

	// Fields
	private PDU pdu;
	protected byte[] header;

	// Constructor
	public PDUDecorator(PDU data) {
		super(data);

		pdu = data;
		header = new byte[] {};
	}

	public PDUDecorator(byte[] data) {
		super(data);

		pdu = new PDU(data);
		header = new byte[] {};
	}

	// Methods
	@Override

	public byte[] getEnhancedData() {
		return getEnhanceDataCore(pdu);
	}

	@Override
	public byte[] getInnerData() {
		return getInnerDataCore(pdu);
	}

	public int getHeaderSize() {
		return header.length + pdu.getHeaderSize();
	}

	protected abstract byte[] getEnhanceDataCore(PDU pdu);

	protected abstract byte[] getInnerDataCore(PDU pdu);
}
