/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Test
 * Filename: SessionManagerTest.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import communication.configurations.IConfiguration;
import communication.managers.TempConfigurationsService;
import communication.managers.IConfigurationManager;
import communication.managers.SessionManager;
import mocks.ConfigurationMock;

public class SessionManagerTest {

	private SessionManager sessionManager;

	@Before
	public void setUp() {
		sessionManager = new SessionManager(new IConfigurationManager() {
			@Override
			public List<IConfiguration> getConfigurations() {
				return new ArrayList<>();
			}

			@Override
			public IConfiguration createConfiguration() {
				return new ConfigurationMock();
			}
		}, new TempConfigurationsService());
	}

	@Test
	public void createNewSessionNumber() {
		int oldSession = 0;
		List<Integer> notAllowedSessions = new ArrayList<>();
		Object newSession = createNewSessionNumber(oldSession, notAllowedSessions);

		assertNotNull(newSession);
		assertTrue(newSession instanceof Integer);
		assertTrue((int) newSession > 0);
		assertTrue((int) newSession <= 255);
		assertNotEquals(oldSession, (int) newSession);

		oldSession = 77;
		newSession = createNewSessionNumber(oldSession, notAllowedSessions);

		assertNotNull(newSession);
		assertTrue(newSession instanceof Integer);
		assertTrue((int) newSession > 0);
		assertTrue((int) newSession <= 255);
		assertNotEquals(oldSession, (int) newSession);
	}

	private Object createNewSessionNumber(int oldSession, List<Integer> notAllowedSessions) {
		try {
			Method method = SessionManager.class.getDeclaredMethod("createNewSessionNumber",
					new Class[] { int.class, List.class });
			method.setAccessible(true);
			return method.invoke(sessionManager, oldSession, notAllowedSessions);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
