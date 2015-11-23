/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: TransportPDUDecorator.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

public class TransportPDU extends PDUDecorator {

	// Constructors
	public TransportPDU(PDU data) {
		super(data);
	}

	public TransportPDU(byte[] data) {
		super(data);
	}

	// Methods
	@Override
	protected byte[] getEnhanceDataCore(PDU pdu) {
		return pdu.getEnhancedData();
	}

	@Override
	protected byte[] getInnerDataCore(PDU pdu) {
		return pdu.getInnerData();
	}
}
