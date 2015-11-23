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
		byte[] data = new byte[] { expectedFlags, expectedId, expectedData[0] };

		SessionPDU decorator = new SessionPDU(new PDU(data));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
		int sessionId = decorator.getSessionId();
		assertEquals(expectedId, sessionId);
		byte flags = decorator.getFlags();
		assertEquals(expectedFlags, flags);
	}

	@Test
	public void PresentationDecoratorInnerData() {
		byte[] expectedFlags = new byte[] { 0b00000000 };
		byte[] expectedData = new byte[] { 0b01010101 };
		byte[] data = new byte[] { expectedFlags[0], expectedData[0] };

		PresentationPDU decorator = new PresentationPDU(new PDU(data));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void ApplicationDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };
		byte expectedCommands = (byte) 0b00000000;
		byte expectedLength = (byte) 0b00000000;
		byte[] data = new byte[] { expectedFlags[0], expectedCommands, expectedLength, expectedData[0] };

		ApplicationPDU decorator = new ApplicationPDU(new PDU(data));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void CombinedDecoratorInnerDataTest() {
		byte[] expectedData = new byte[] { 0b01010101 };
		byte sessionFlags = (byte) 0b00000011;
		byte sessionId = (byte) 0b00000011;
		byte presentationFlags = (byte) 0b00110000;
		byte applicationFlags = (byte) 0b00001100;
		byte applicationCommands = (byte) 0b00000100;
		byte applicationLength = (byte) 0b00000010;

		byte[] data = new byte[] { sessionFlags, sessionId, presentationFlags, applicationFlags, applicationCommands,
				applicationLength, expectedData[0] };

		NetworkPDU combinedDecorator = new NetworkPDU(
				new TransportPDU(new SessionPDU(new PresentationPDU(new ApplicationPDU(new PDU(data))))));

		byte[] actualData = combinedDecorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}
}
