package communication.pdu;

public class PDU {

	// fields
	protected byte[] data;

	// ctor
	public PDU(String data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		
		this.data = data.getBytes();
	}
	
	public PDU(byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		
		this.data = data;
	}

	public PDU(PDU data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		
		// Important call getData
		this.data = data.getData();
	}
	
	// methods
	public byte[] getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
