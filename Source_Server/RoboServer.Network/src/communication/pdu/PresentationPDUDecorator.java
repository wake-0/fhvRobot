package communication.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PresentationPDUDecorator extends PDUDecorator {

	private byte[] flags;

	public PresentationPDUDecorator(PDU data) {
		super(data);

		flags = new byte[] { 0b00000000 };
	}

	@Override
	protected byte[] enhanceData(PDU packet) {
		try {

			// Add flag bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(flags);
			outputStream.write(packet.getEnhancedData());
			return outputStream.toByteArray();

		} catch (IOException e) {
			return data;
		}
	}

	@Override
	protected byte[] innerData(PDU packet) {
		// Remove the flag bytes
		byte[] data = packet.getInnerData();
		return Arrays.copyOfRange(data, flags.length, data.length);
	}

}
