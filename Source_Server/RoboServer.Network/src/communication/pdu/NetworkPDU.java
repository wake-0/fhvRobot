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

public class NetworkPDU extends PDUDecorator {

	// Constructors
	public NetworkPDU(PDU pdu) {
		super(pdu);
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
