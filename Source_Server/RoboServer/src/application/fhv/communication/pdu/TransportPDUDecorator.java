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
	protected byte[] enhanceData(byte[] data) {
		return data;
	}

	@Override
	protected byte[] innerData(byte[] data) {
		return data;
	}
}
