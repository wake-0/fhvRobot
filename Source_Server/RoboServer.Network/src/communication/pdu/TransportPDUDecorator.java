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

public class TransportPDUDecorator extends PDUDecorator {

	// Constructors
	public TransportPDUDecorator(PDU data) {
		super(data);
	}

	public TransportPDUDecorator(byte[] data) {
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
