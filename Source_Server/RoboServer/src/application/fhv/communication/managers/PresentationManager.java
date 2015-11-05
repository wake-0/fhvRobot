package communication.managers;

import com.google.inject.Singleton;

@Singleton
public class PresentationManager extends LayerManager<ClientType>{

	@Override
	protected ClientType getDefaultValue() {
		return ClientType.ROBO;
	}
}
