package communication.pdu;

public abstract class PDUDecorator extends PDU {

	public PDUDecorator(PDU data) {
		super(data);
	}

	@Override
	public byte[] getData() {
		return enhanceData(data);
		
	}
	
	protected abstract byte[] enhanceData(byte[] data);
	
}
