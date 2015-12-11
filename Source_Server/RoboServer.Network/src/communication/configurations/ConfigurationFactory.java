package communication.configurations;

import java.net.DatagramPacket;

public class ConfigurationFactory {

	public static Configuration createConfiguration(DatagramPacket packet) {
		if (packet == null) {
			throw new IllegalArgumentException();
		}

		return new Configuration(0, packet.getPort(), packet.getAddress().getHostName());
	}
}
