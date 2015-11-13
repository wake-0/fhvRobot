package communication.pdu;

public class ApplicationPDUDecorator extends PDUDecorator {

	public ApplicationPDUDecorator(PDU data) {
		super(data);
	}

	@Override
	protected byte[] enhanceData(byte[] data) {
		return data;
	}

	@Override
	protected byte[] innerData(byte[] data) {
		// TODO Auto-generated method stub
		return data;
	}

}
