package communication.pdu;

import java.net.InetAddress;

public class NetworkPDUDecorator extends PDUDecorator {

	// fields
	private InetAddress ipAddress;
	
	// ctor
	public NetworkPDUDecorator(InetAddress ipAddress, PDU data) {
		super(data);
		
		this.ipAddress = ipAddress;
	}

	// methods
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	@Override
	protected byte[] enhanceData(byte[] data) {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	protected byte[] innerData(byte[] data) {
		// TODO Auto-generated method stub
		return data;
	}

}
