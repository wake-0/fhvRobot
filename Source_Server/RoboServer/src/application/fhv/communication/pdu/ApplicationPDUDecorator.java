package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ApplicationPDUDecorator extends PDUDecorator {

	private byte[] flags;
	
	public ApplicationPDUDecorator(PDU data) {
		super(data);
		
		flags = new byte[] { 0b00000000 };
	}

	@Override
	protected byte[] enhanceData(byte[] data) {
		try {

			// Add flag bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(flags);
			outputStream.write(data);
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] innerData(byte[] data) {
		return Arrays.copyOfRange(data, flags.length, data.length);
	}
}
