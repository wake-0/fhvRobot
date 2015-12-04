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

import java.io.PrintStream;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.TextFlow;
import models.Client;
import network.NetworkServer;
import utils.ErrorServerOutputPrinter;
import utils.ServerOutputPrinter;

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
	private TextFlow taServerOutput;

	// Methods
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			roboController = tab1Controller.getRoboController();
			appController = tab2Controller.getAppController();

			this.server = new NetworkServer(roboController, appController);

			tab1Controller.setServer(server);
			tab2Controller.setServer(server);

			PrintStream outputStream = new PrintStream(new ServerOutputPrinter(taServerOutput));
			PrintStream errorOutputStream = new PrintStream(new ErrorServerOutputPrinter(taServerOutput));

			System.setOut(outputStream);
			System.setErr(errorOutputStream);

			System.out.println("System output redirected ...");
			System.err.println("System error redirected ...");

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		server.shutdown();
	}
}
