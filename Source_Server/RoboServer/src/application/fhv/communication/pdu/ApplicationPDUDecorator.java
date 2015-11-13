package communication.pdu;

public class ApplicationPDUDecorator extends PDUDecorator {

	private byte[] flags;
	
	public ApplicationPDUDecorator(PDU data) {
		super(data);
		
		flags = new byte[] { 0b00000000 };
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
