package communication.pdu;

public class TransportPDUDecorator extends PDUDecorator {

	private int port;
	
	public TransportPDUDecorator(int port, PDU data) {
		super(data);
		this.port = port;
	}

	public int getPort() {
		return port;
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
