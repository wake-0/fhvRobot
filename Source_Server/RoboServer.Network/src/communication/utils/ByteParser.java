package communication.utils;

import java.nio.ByteBuffer;

public class ByteParser {

	public static byte intToByte(int value) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
		return bytes[3];
	}

}
