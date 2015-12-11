/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: MainWindowController.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package controllers;

import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import models.Client;
import network.NetworkServer;

public class MainWindowController implements Initializable {

	// Fields
	private NetworkServer server;

	private ClientController<Client> roboController;
	private ClientController<Client> appController;

	@FXML
	private AppTabPageController tab2Controller;
	@FXML
	private RoboTabPageController tab1Controller;

	@FXML
	private TextArea taServerOutput;

	// Methods
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			roboController = tab1Controller.getRoboController();
			appController = tab2Controller.getAppController();

			this.server = new NetworkServer(roboController, appController);

			tab1Controller.setServer(server);
			tab2Controller.setServer(server);

			// PrintStream outputStream = new PrintStream(new
			// ServerOutputPrinter(taServerOutput));
			// System.setOut(outputStream);
			System.out.println("System output redirected ...");

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		server.shutdown();
	}
}
