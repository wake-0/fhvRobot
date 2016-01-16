package controllers;

public class PersistencyController {

	private byte[] persistendData;
	
	public PersistencyController() {
		persistendData = new byte[65535];
	}
	
	public void setPersistentData(byte[] persistentData) {
		this.persistendData = persistentData;
		// TODO: write to xml file without save file dialog
	}
	
	public byte[] getPersistentData() {
		return persistendData;
	}
	
	// TODO: xml persisting
}
