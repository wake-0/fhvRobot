/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Test
 * Filename: DecoratorInnerDataTests.java
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

public class DecoratorInnerDataTests {

	@Test
	public void NetworkDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		NetworkPDU decorator = new NetworkPDU(new PDU(expectedData));

		byte[] data = decorator.getInnerData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void TransportDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		TransportPDU decorator = new TransportPDU(new PDU(expectedData));

		byte[] data = decorator.getInnerData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void SessionDecoratorInnerData() {
		byte expectedId = (byte) 0b00000000;
		byte expectedFlags = (byte) 0b00000000;
		byte[] expectedData = new byte[] { 0b01010101 };

		SessionPDU decorator = new SessionPDU(expectedFlags, expectedId, new PDU(expectedData));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);

		int sessionId = decorator.getSessionId();
		assertEquals(expectedId, sessionId);

		byte flags = decorator.getFlags();
		assertEquals(expectedFlags, flags);
	}

	@Test
	public void PresentationDecoratorInnerData() {
		byte expectedFlags = (byte) 0b00000000;
		byte[] expectedData = new byte[] { 0b01010101 };

		PresentationPDU decorator = new PresentationPDU(expectedFlags, new PDU(expectedData));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void ApplicationDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		byte expectedCommands = (byte) 0b00000000;
		byte expectedFlags = (byte) 0b00000000;

		ApplicationPDU decorator = new ApplicationPDU(expectedFlags, expectedCommands, new PDU(expectedData));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void CombinedDecoratorInnerDataTest() {
		byte[] expectedData = new byte[] { 0b01010101 };

		NetworkPDU combinedDecorator = new NetworkPDU(
				new TransportPDU(new SessionPDU(new PresentationPDU(new ApplicationPDU(new PDU(expectedData))))));

		byte[] actualData = combinedDecorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}
}
