package communication.managers;

import com.google.inject.Singleton;

@Singleton
public class PresentationManager extends LayerManager<String>{

	@Override
	protected String getDefaultValue() {
		return "";
	}
}
