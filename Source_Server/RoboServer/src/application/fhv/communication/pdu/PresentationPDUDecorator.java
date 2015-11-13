package communication.pdu;

public class PresentationPDUDecorator extends PDUDecorator {

	public PresentationPDUDecorator(PDU data) {
		super(data);
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
