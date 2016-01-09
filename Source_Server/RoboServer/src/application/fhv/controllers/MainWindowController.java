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
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import models.Client;
import network.IClientController;
import network.NetworkServer;

public class MainWindowController implements Initializable {

	// Fields
	private NetworkServer server;

	private IClientController<Client> roboController;
	private IClientController<Client> appController;
	private IClientController<Client> gamingController;

	@FXML
	private AppTabPageController appViewController;
	@FXML
	private RoboTabPageController roboViewController;

	@FXML
	private RobotViewController robotViewController;

	@FXML
	private TextArea taServerOutput;

	// Methods
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			roboController = roboViewController.getRoboController();
			appController = appViewController.getAppController();

			// Gaming client
			Client client = new Client();
			gamingController = new SingleClientController(client);

			this.server = new NetworkServer(roboController, appController, gamingController);

			roboViewController.setServer(server);
			roboViewController.setRobotViewController(robotViewController);
			appViewController.setServer(server);

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

	public void postInitialize(Scene s) {
		robotViewController.setupListeners(s);
	}
}
