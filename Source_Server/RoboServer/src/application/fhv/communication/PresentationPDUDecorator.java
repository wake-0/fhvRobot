package communication;

public class PresentationPDUDecorator extends PDUDecorator {

	public PresentationPDUDecorator(PDU data) {
		super(data);
	}

	@Override
	protected byte[] enhanceData(byte[] data) {
		// TODO Auto-generated method stub
		return data;
	}

}
