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

import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

public class DecoratorEnhanceDataTests {

	@Test
	public void NetworkDecoratorEnhanceData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		NetworkPDUDecorator decorator = new NetworkPDUDecorator(new PDU(expectedData));

		byte[] data = decorator.getEnhancedData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void TransportDecoratorEnhanceData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		TransportPDUDecorator decorator = new TransportPDUDecorator(new PDU(expectedData));

		byte[] data = decorator.getEnhancedData();
		assertArrayEquals(expectedData, data);
	}

	@Test
	public void SessionDecoratorEnhanceData() {
		byte data = (byte) 0b01010101;
		byte expectedId = (byte) 0b00000000;
		byte expectedFlags = (byte) 0b00000000;
		byte[] expectedData = new byte[] { expectedFlags, expectedId, data };

		SessionPDUDecorator decorator = new SessionPDUDecorator(new PDU(new byte[] { data }));

		byte[] actualData = decorator.getEnhancedData();
		assertArrayEquals(expectedData, actualData);

		byte sessionId = decorator.getSessionId();
		assertEquals(expectedId, sessionId);

		byte flags = decorator.getFlags();
		assertEquals(expectedFlags, flags);
	}

	@Test
	public void PresentationDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };

		PresentationPDUDecorator decorator = new PresentationPDUDecorator(new PDU(data));

		byte[] expectedData = new byte[] { expectedFlags[0], data[0] };
		byte[] actualData = decorator.getEnhancedData();

		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void ApplicationDecoratorEnhanceData() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] { 0b00000000 };
		byte expectedCommands = (byte) 0b00000000;
		byte expectedLength = (byte) 0b00000000;

		ApplicationPDUDecorator decorator = new ApplicationPDUDecorator(new PDU(data));

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
		byte applicationLength = (byte) 0b00000000;
		byte[] expectedData = new byte[] { sessionFlags, sessionId, presentationFlags, applicationFlags,
				applicationCommands, applicationLength, data[0] };

		NetworkPDUDecorator combinedDecorator = new NetworkPDUDecorator(new TransportPDUDecorator(
				new SessionPDUDecorator(new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(data))))));

		byte[] actualData = combinedDecorator.getEnhancedData();
		assertArrayEquals(expectedData, actualData);
	}
}
