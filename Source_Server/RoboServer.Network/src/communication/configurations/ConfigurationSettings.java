package communication.configurations;

public class ConfigurationSettings {

	public static int MAX_CONFIGURATION_COUNT = 128;

	// Session Layer
	// Session id
	public static int NOT_ALLOWED_SESSION_ID = -1;
	public static int DEFAULT_SESSION_ID = 0;

	// Flags
	public static int NO_FREE_SESSION_FLAGS = 0;
	public static int DEFAULT_SESSION_FLAGS = 0;
	public static int REQUEST_SESSION_FLAGS = 1;

	// Application Layer
	public static int DEFAULT_APPLICATION_FLAGS = 0;
	public static int DEFAULT_APPLICATION_COMMAND = 0;

	public static int MIN_SESSION_NUMBER = 1;
	public static int MAX_SESSION_NUMBER = 127;

	public static String OPEN_MESSAGE = "OPEN";
}
