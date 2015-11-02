package communication.managers;

import java.net.InetAddress;

public class NetworkManager extends LayerManager<InetAddress> {

	@Override
	protected InetAddress getDefaultValue() {
		return InetAddress.getLoopbackAddress();
	}
}
