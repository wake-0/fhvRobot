package communication.managers;

import java.net.DatagramPacket;
import java.util.List;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClientConfiguration;

@Singleton
public class NetworkManager extends LayerManager {

	@Inject
	public NetworkManager(IClientManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		String ipAddress = packet.getAddress().getHostName();
		
		List<IClientConfiguration> configurations = manager.getConfigurations();
		Optional<IClientConfiguration> configuration = configurations.stream().filter(c -> c.getIpAddress().equals(ipAddress)).findFirst();
		IClientConfiguration currentConfiguration = configuration.isPresent() ? configuration.get() : null;
		
		if (currentConfiguration == null) 
		{
			currentConfiguration = manager.createClientConfiguration();
			currentConfiguration.setIpAddress(ipAddress);
		}
		
		currentClientService.setClient(currentConfiguration);
		return false;
	}
}
