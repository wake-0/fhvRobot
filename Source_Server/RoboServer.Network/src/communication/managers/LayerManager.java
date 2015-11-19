package communication.managers;

public abstract class LayerManager implements IDataReceivedHandler {

	// fields
	protected IClientManager manager;
	protected CurrentConfigurationService currentClientService;
	
	// Constructor
	public LayerManager(IClientManager manager, CurrentConfigurationService currentClientService) {
		this.manager = manager;
		this.currentClientService = currentClientService;
	}
}
