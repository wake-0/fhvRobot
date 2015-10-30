package communication;

import java.net.InetAddress;

public class NetworkPDUDecorator extends PDUDecorator {

	// fields
	private InetAddress ipAddress;
	
	// ctor
	public NetworkPDUDecorator(PDU data, InetAddress ipAddress) {
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

}
