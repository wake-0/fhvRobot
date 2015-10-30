package communication;

public class TransportPDUDecorator extends PDUDecorator {

	private int port;
	
	public TransportPDUDecorator(PDU data, int port) {
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

}
