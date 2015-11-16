package communication.pdu;

public class NetworkPDUDecorator extends PDUDecorator {

	// fields
	private String ipAddress;
	
	// ctor
	public NetworkPDUDecorator(PDU data) {
		super(data);
	}
	
	public NetworkPDUDecorator(String string, PDU data) {
		super(data);
		
		this.ipAddress = string;
	}

	// methods
	public String getIpAddress() {
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
