package main;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		try {
			URL mainWindow = getClass().getResource("/views/MainWindow.fxml");
			URL stylesheet = getClass().getResource("/views/theme.css");
			
			// Root window
			Parent root = FXMLLoader.load(mainWindow);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(stylesheet.toExternalForm());
			
			// Default stage settings
			stage.setTitle("FHV Robo Server");
			stage.setScene(scene);
			
			// Show stage
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
