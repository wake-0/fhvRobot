package communication;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
		try {
			byte[] expectedData = new byte[] { 0b01010101 };
			InetAddress expectedAddress = InetAddress.getByName("127.0.0.1");
			NetworkPDUDecorator decorator = new NetworkPDUDecorator(expectedAddress, new PDU(expectedData));

			byte[] data = decorator.getInnerData();
			assertArrayEquals(expectedData, data);

			InetAddress actualAddress = decorator.getIpAddress();
			assertEquals(expectedAddress, actualAddress);
		} catch (UnknownHostException e) {
			fail();
		}
	}
	
	@Test
	public void TransportDecoratorInnerData() {
		byte[] expectedData = new byte[] { 0b01010101 };
		int expectedPort = 12;
		TransportPDUDecorator decorator = new TransportPDUDecorator(expectedPort, new PDU(expectedData));

		byte[] data = decorator.getInnerData();
		int port = decorator.getPort();
		assertArrayEquals(expectedData, data);
		assertEquals(expectedPort, port);
	}
	
	@Test
	public void SessionDecoratorInnerData() {
		byte[] expectedId = new byte[] { 0b00000000 };
		byte[] expectedFlags = new byte[] { 0b00000000 };
		byte[] expectedData = new byte[] { 0b01010101};
		byte[] data = new byte[] { expectedFlags[0], expectedId[0], expectedData[0] };
		
		SessionPDUDecorator decorator = new SessionPDUDecorator(new PDU(data));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
		byte[] sessionId = decorator.getSessionId();
		assertArrayEquals(expectedId, sessionId);
		byte[] flags = decorator.getFlags();
		assertArrayEquals(expectedFlags, flags);
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
		byte[] data = new byte[] { expectedFlags[0], expectedData[0] };

		ApplicationPDUDecorator decorator = new ApplicationPDUDecorator(new PDU(data));

		byte[] actualData = decorator.getInnerData();
		assertArrayEquals(expectedData, actualData);
	}

	@Test
	public void CombinedDecoratorInnerDataTest() {
		try {
			byte[] expectedData = new byte[] { 0b01010101 };
			InetAddress address = InetAddress.getByName("127.0.0.1");
			int port = 77;

			byte[] sessionFlags = new byte[] { 0b00000000 };
			byte[] sessionId = new byte[] { 0b00000000 };
			byte[] presentationFlags = new byte[] { 0b00000000 };
			byte[] applicationFlags = new byte[] { 0b00000000 };
			byte[] data = new byte[] { sessionFlags[0], sessionId[0], presentationFlags[0], applicationFlags[0], expectedData[0] };
			
			NetworkPDUDecorator combinedDecorator = new NetworkPDUDecorator(address, new TransportPDUDecorator(port,
					new SessionPDUDecorator(new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(data))))));

			byte[] actualData = combinedDecorator.getInnerData();
			assertArrayEquals(expectedData, actualData);
		} catch (UnknownHostException e) {
			fail();
		}
	}
}
