package communication.managers;

import com.google.inject.Singleton;

@Singleton
public class TransportManager extends LayerManager<Integer> {

	@Override
	protected Integer getDefaultValue() {
		return -1;
	}
}
