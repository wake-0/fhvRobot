package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class MainWindowController implements Initializable {

	@FXML
	private Button btnKill;
	@FXML
	private Button btnUp;
	@FXML
	private Button btnDown;
	
	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
	}
	
	@FXML
	private void handleUpClick() {
		System.out.println("button up clicked.");
	}
	
	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
