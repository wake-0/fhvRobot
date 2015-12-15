/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: Main.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package main;

import java.net.URL;

import controllers.MainWindowController;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
			FXMLLoader loader = new FXMLLoader(mainWindow);

			Scene scene = new Scene(loader.load());

			scene.getStylesheets().add(stylesheet.toExternalForm());
			stage.addEventHandler(javafx.stage.WindowEvent.WINDOW_HIDING, new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					MainWindowController controller = loader.getController();
					controller.shutdown();
					System.exit(0);
				}
			});

			// Default stage settings
			stage.setTitle("FHV Robo Server");
			stage.setScene(scene);

			// Show stage
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
