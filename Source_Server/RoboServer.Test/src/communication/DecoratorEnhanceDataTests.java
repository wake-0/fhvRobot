/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Test
 * Filename: DecoratorEnhanceDataTests.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import communication.pdu.ApplicationPDU;
import communication.pdu.NetworkPDU;
import communication.pdu.PDU;
import communication.pdu.PresentationPDU;
import communication.pdu.SessionPDU;
import communication.pdu.TransportPDU;

public class DecoratorEnhanceDataTests {

	@Test
	public void NetworkDecoratorEnhanceData() {
		byte[] innerData = new byte[] { 0b01010101 };
		NetworkPDU decorator = new NetworkPDU(new PDU(innerData));

		byte[] expectedData = new byte[] { 1, innerData[0] };
		byte[] data = decorator.getEnhancedData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void TransportDecoratorEnhanceData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		TransportPDU decorator = new TransportPDU(new PDU(expectedData));

		byte[] data = decorator.getEnhancedData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void SessionDecoratorEnhanceData() {
		byte data = (byte) 0b01010101;
		byte expectedId = (byte) 0b00000000;
		byte expectedFlags = (byte) 0b00000000;
		byte[] expectedData = new byte[] { expectedFlags, expectedId, data };

		SessionPDU decorator = new SessionPDU(new PDU(new byte[] { data }));

		byte[] actualData = decorator.getEnhancedData();
		assertArrayEquals(expectedData, actualData);

		int sessionId = decorator.getSessionId();
		assertEquals(expectedId, sessionId);

		byte flags = decorator.getFlags();
		assertEquals(expectedFlags, flags);
	}

	@Test
	public void PresentationDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };

		PresentationPDU decorator = new PresentationPDU(new PDU(data));

		byte[] expectedData = new byte[] { expectedFlags[0], data[0] };
		byte[] actualData = decorator.getEnhancedData();

		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void ApplicationDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };
		byte expectedCommands = (byte) 0b00000000;
		byte expectedLength = (byte) 0b00000001;

		ApplicationPDU decorator = new ApplicationPDU(new PDU(data));

		byte[] expectedData = new byte[] { expectedFlags[0], expectedCommands, expectedLength, data[0] };
		byte[] actualData = decorator.getEnhancedData();

		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void CombinedDecoratorEnhanceDataTest() {
		byte[] data = new byte[] { 0b01010101 };
		byte sessionFlags = (byte) 0b00000000;
		byte sessionId = (byte) 0b00000000;
		byte presentationFlags = (byte) 0b00000000;
		byte applicationFlags = (byte) 0b00000000;
		byte applicationCommands = (byte) 0b00000000;
		byte applicationLength = (byte) 0b00000001;
		byte length = (byte) 7;
		byte[] expectedData = new byte[] { length, sessionFlags, sessionId, presentationFlags, applicationFlags,
				applicationCommands, applicationLength, data[0] };

		NetworkPDU combinedDecorator = new NetworkPDU(
				new TransportPDU(new SessionPDU(new PresentationPDU(new ApplicationPDU(new PDU(data))))));

		byte[] actualData = combinedDecorator.getEnhancedData();
		assertArrayEquals(expectedData, actualData);
	}
}
