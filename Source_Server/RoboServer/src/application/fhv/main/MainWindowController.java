package main;

import javafx.scene.control.Button;

public class MainWindowController {

	public Button btnKill;
	
	public void handleKillClick() {
		System.out.println("button clicked.");
		btnKill.setText("btn clicked.");
	}
	
}
