package communication.managers;

import com.google.inject.Singleton;

@Singleton
public class ApplicationManager extends LayerManager<String> {

	@Override
	protected String getDefaultValue() {
		return "";
	}

}
