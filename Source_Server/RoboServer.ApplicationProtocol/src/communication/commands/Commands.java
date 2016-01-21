package communication.commands;

public class Commands {

	public static final int DEFAULT = 0;

	public static final int CHANGE_NAME = 1;
	public static final int GENERAL_MESSAGE = 2;

	public static final int DRIVE_LEFT = 11;
	public static final int DRIVE_RIGHT = 10;
	public static final int DRIVE_BOTH = 12;
	public static final int TRIGGER_LED = 18;

	public static final int ROBO_STEARING = 50;
	public static final int ROBO_NOT_STEARING = 51;

	public static final int REQUEST_DISCONNECT = 77;
	public static final int DISCONNECTED = 78;

	public static final int CAMERA_ON = 20;
	public static final int CAMERA_OFF = 21;

	public static final int REQUEST_OPERATOR = 30;
	
	public static final int PERSIST_DATA = 60;
	public static final int REQUEST_PERSISTED_DATA = 61;

	public static final int ORIENTATION_DATA = 72; 
}
