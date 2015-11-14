package communication;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

public class DecoratorTests {

	@Test
	public void NetworkDecorator() {
		try {
			byte[] expectedData = new byte[] { 0b01010101 };
			InetAddress expectedAddress = InetAddress.getByName("127.0.0.1");
			NetworkPDUDecorator decorator = new NetworkPDUDecorator(expectedAddress, new PDU(expectedData));
			
			byte[] data = decorator.getData();
			assertArrayEquals(expectedData, data);
			
			InetAddress actualAddress = decorator.getIpAddress();
			assertEquals(expectedAddress, actualAddress);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
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
		byte[] expectedId = new byte[] { 0b00000000 };
		byte[] expectedFlags = new byte[] { 0b00000000 };
		
		SessionPDUDecorator decorator = new SessionPDUDecorator(new PDU(data));
		
		byte[] expectedData = new byte[] { expectedId[0], expectedFlags[0], data[0] };
		byte[] actualData = decorator.getData();
		assertArrayEquals(expectedData, actualData);
		byte[] sessionId = decorator.getSessionId();
		assertArrayEquals(expectedId, sessionId);
		byte[] flags = decorator.getFlags();
		assertArrayEquals(expectedFlags, flags);
	}
	
	@Test
	public void PresentationDecorator() {
		byte[] data = new byte[] { 0b01010101 };
		byte[] expectedFlags = new byte[] {0b00000000 };
		
		PresentationPDUDecorator decorator = new PresentationPDUDecorator(new PDU(data));
		
		byte[] expectedData = new byte[] {expectedFlags[0], data[0] };
		byte[] actualData = decorator.getData();
		
		assertArrayEquals(expectedData, actualData);
	}
	
	@Test
	public void ApplicationDecorator() {
		byte[] expectedData = new byte[] { 0b01010101 };
		ApplicationPDUDecorator decorator = new ApplicationPDUDecorator(new PDU(expectedData));
		
		byte[] data = decorator.getData();
		assertArrayEquals(expectedData, data);
	}
}
