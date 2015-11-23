package communication.utils;

import java.nio.ByteBuffer;

public class NumberParser {

	public static byte intToByte(int value) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
		return bytes[3];
	}

	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
