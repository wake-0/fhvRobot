/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: PDU.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.pdu;

public class PDU {

	// Fields
	protected byte[] data;

	// Constructors
	public PDU(String data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}

		this.data = data.getBytes();
	}

	public PDU(byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}

		this.data = data;
	}

	public PDU(PDU data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}

		// Important call getData
		this.data = data.getData();
	}

	// Methods
	public byte[] getData() {
		return data;
	}

	public byte[] getEnhancedData() {
		return data;
	}

	public byte[] getInnerData() {
		return data;
	}

	public int getHeaderSize() {
		return 0;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
