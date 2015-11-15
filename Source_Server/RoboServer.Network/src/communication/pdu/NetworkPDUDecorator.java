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
	protected byte[] enhanceData(PDU packet) {
		return packet.getEnhancedData();
	}

	@Override
	protected byte[] innerData(PDU packet) {
		return packet.getInnerData();
	}

}
