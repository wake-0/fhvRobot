package communication.pdu;

public abstract class PDUDecorator extends PDU {
	
	public PDUDecorator(PDU data) {
		super(data);
	}

	@Override
	public byte[] getEnhancedData() {
		return enhanceData(data);
	}
	
	public byte[] getInnerData() {
		return innerData(data);
	}
	
	protected abstract byte[] enhanceData(byte[] data);
	
	protected abstract byte[] innerData(byte[] data);
	
}
