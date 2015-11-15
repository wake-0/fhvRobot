package communication.pdu;

public abstract class PDUDecorator extends PDU {
	
	private PDU packet;
	
	public PDUDecorator(PDU data) {
		super(data);
		packet = data;
	}

	@Override
	public byte[] getEnhancedData() {
		return enhanceData(packet);
	}
	
	public byte[] getInnerData() {
		return innerData(packet);
	}
	
	protected abstract byte[] enhanceData(PDU packet);
	
	protected abstract byte[] innerData(PDU packet);
	
}
