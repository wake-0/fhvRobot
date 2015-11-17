package communication;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import communication.managers.CurrentConfigurationService;
import communication.managers.IClientManager;
import communication.managers.SessionManager;
import mocks.ConfigurationMock;

public class SessionManagerTest {

	private SessionManager sessionManager;

	@Before
	public void setUp() {
		sessionManager = new SessionManager(new IClientManager() {
			@Override
			public List<IClientConfiguration> getConfigurations() {
				return new ArrayList<>();
			}

			@Override
			public IClientConfiguration createClientConfiguration() {
				return new ConfigurationMock();
			}
		}, new CurrentConfigurationService());
	}

	@Test
	public void createNewSessionNumber() {
		int oldSession = 0;
		Object newSession = createNewSessionNumber(oldSession);

		assertNotNull(newSession);
		assertTrue(newSession instanceof Integer);
		assertTrue((int) newSession >= 0);
		assertTrue((int) newSession <= 255);
		assertNotEquals(oldSession, (int) newSession);

		oldSession = 77;
		newSession = createNewSessionNumber(oldSession);

		assertNotNull(newSession);
		assertTrue(newSession instanceof Integer);
		assertTrue((int) newSession >= 0);
		assertTrue((int) newSession <= 255);
		assertNotEquals(oldSession, (int) newSession);
	}

	private Object createNewSessionNumber(int oldSession) {
		try {
			Method method = SessionManager.class.getDeclaredMethod("createNewSessionNumber", new Class[] { int.class });
			method.setAccessible(true);
			return method.invoke(sessionManager, oldSession);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
