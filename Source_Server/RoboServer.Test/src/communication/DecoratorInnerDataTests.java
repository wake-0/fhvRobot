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

import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

public class DecoratorInnerDataTests {

	@Test
	public void NetworkDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		NetworkPDUDecorator decorator = new NetworkPDUDecorator(new PDU(expectedData));

		byte[] data = decorator.getInnerData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void TransportDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		TransportPDUDecorator decorator = new TransportPDUDecorator(new PDU(expectedData));

		byte[] data = decorator.getInnerData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void SessionDecoratorInnerData() {
		byte expectedId = (byte) 0b00000000;
		byte expectedFlags = (byte) 0b00000000;
		byte[] expectedData = new byte[] { 0b01010101 };
		byte[] data = new byte[] { expectedFlags, expectedId, expectedData[0] };

		SessionPDUDecorator decorator = new SessionPDUDecorator(new PDU(data));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
		byte sessionId = decorator.getSessionId();
		assertEquals(expectedId, sessionId);
		byte flags = decorator.getFlags();
		assertEquals(expectedFlags, flags);
	}

	@Test
	public void PresentationDecoratorInnerData() {
		byte[] expectedFlags = new byte[] { 0b00000000 };
		byte[] expectedData = new byte[] { 0b01010101 };
		byte[] data = new byte[] { expectedFlags[0], expectedData[0] };

		PresentationPDUDecorator decorator = new PresentationPDUDecorator(new PDU(data));

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

		ApplicationPDUDecorator decorator = new ApplicationPDUDecorator(new PDU(data));

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

		NetworkPDUDecorator combinedDecorator = new NetworkPDUDecorator(new TransportPDUDecorator(
				new SessionPDUDecorator(new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(data))))));

		byte[] actualData = combinedDecorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}
}
