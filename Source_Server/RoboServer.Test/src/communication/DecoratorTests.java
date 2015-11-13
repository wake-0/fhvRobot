package communication;

import static org.junit.Assert.*;

import org.junit.Test;

import communication.pdu.PDU;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

public class DecoratorTests {

	@Test
	public void TransportDecorator() {
		
		byte[] expectedData = new byte[] { 0b01010101 };
		int expectedPort = 12;
		TransportPDUDecorator decorator = new TransportPDUDecorator(expectedPort, new PDU(expectedData));
		
		byte[] data = decorator.getData();
		int port = decorator.getPort();
		assertArrayEquals(expectedData, data);
		assertEquals(expectedPort, port);
	}

	@Test
	public void SessionDecorator() {
		
		byte[] data = new byte[] { 0b01010101 };
		SessionPDUDecorator decorator = new SessionPDUDecorator(new PDU(data));
		byte[] sessionId = decorator.getSessionId();
		byte[] flags = decorator.getFlags();
		
		byte[] expectedData = new byte[] { 0b00000000, 0b00000000, 0b01010101 };
		byte[] actualData = decorator.getData();
		assertArrayEquals(expectedData, actualData);
	}
	
}
