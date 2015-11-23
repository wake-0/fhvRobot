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

public class NetworkPDUDecorator extends PDUDecorator {

	// Constructors
	public NetworkPDUDecorator(PDU data) {
		super(data);
	}

	public NetworkPDUDecorator(byte[] data) {
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
