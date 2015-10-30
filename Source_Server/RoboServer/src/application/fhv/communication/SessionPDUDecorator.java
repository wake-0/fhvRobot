package communication;

public class SessionPDUDecorator extends PDUDecorator {

	public SessionPDUDecorator(PDU data) {
		super(data);
	}

	@Override
	protected byte[] enhanceData(byte[] data) {
		// TODO Auto-generated method stub
		return data;
	}

}
