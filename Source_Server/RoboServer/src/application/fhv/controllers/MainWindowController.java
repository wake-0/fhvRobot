package controllers;

import javafx.scene.control.Button;

public class MainWindowController {

	public Button btnKill;
	public Button btnUp;
	public Button btnDown;
	
	public void handleKillClick() {
		System.out.println("button kill clicked.");
	}
	
	public void handleUpClick() {
		System.out.println("button up clicked.");
	}
	
	public void handleDownClick() {
		System.out.println("button down clicked.");
	}
}
