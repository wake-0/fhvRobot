package communication;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

public class SessionManagerTest {

	private SessionManager sessionManager;

	@Before
	public void setUp() {
		sessionManager = SessionManager.getInstance();
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
