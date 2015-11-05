package communication.managers;

import java.net.InetAddress;

import com.google.inject.Singleton;

@Singleton
public class NetworkManager extends LayerManager<InetAddress> {

	@Override
	protected InetAddress getDefaultValue() {
		return InetAddress.getLoopbackAddress();
	}
}
