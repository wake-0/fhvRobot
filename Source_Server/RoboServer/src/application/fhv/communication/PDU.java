package communication;

public class PDU {

	protected byte[] data;

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
		
		this.data = data.data;
	}
	
	public byte[] getData() {
		return data;
	}
}
