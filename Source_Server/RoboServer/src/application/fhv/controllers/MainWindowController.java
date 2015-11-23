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

import controllers.factory.IClientFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.Client;
import models.ClientFactory;
import network.NetworkServer;

public class MainWindowController implements Initializable {

	// fields
	private NetworkServer server;

	private ClientController<Client> robos;
	private ClientController<Client> apps;

	private Client selectedRoboClient;

	// FXML fields
	@FXML
	private TableView<Client> tvClients;
	@FXML
	private TableColumn<Client, Number> tcId;
	@FXML
	private TableColumn<Client, String> tcName;
	@FXML
	private TableColumn<Client, String> tcIp;
	@FXML
	private TextField tfSend;
	@FXML
	private TextField tfReceive;
	@FXML
	private TextField tfName;
	// @FXML
	// private MediaPlayer mediaPlayer;

	// methods
	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
		if (selectedRoboClient != null) {
			robos.removeClient(selectedRoboClient);
		}
	}

	@FXML
	private void handleUpClick() {
		System.out.println("button up clicked.");
	}

	@FXML
	private void handleSendClick() {
		System.out.println("button send clicked.");
		if (selectedRoboClient != null) {
			server.sendToRobo(selectedRoboClient);
		}
	}

	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		IClientFactory<Client> factory = new ClientFactory();
		robos = new ClientController<Client>(factory);
		apps = new ClientController<Client>(factory);

		// Initialize the person table with the two columns.
		tcId.setCellValueFactory(cellData -> cellData.getValue().IdProperty());
		tcName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		tcIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());

		tvClients.setItems(robos.getClients());
		tvClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
			@Override
			public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
				if (newValue != null) {
					tfName.textProperty().bindBidirectional(newValue.NameProperty());
					tfSend.textProperty().bindBidirectional(newValue.SendDataProperty());
					tfReceive.textProperty().bind(newValue.ReceiveDataProperty());
				} else if (oldValue != null) {
					tfSend.textProperty().unbindBidirectional(oldValue.SendDataProperty());
					tfName.textProperty().unbindBidirectional(oldValue.NameProperty());
					tfReceive.textProperty().unbind();
				}

				selectedRoboClient = newValue;
			}
		});

		try {
			this.server = new NetworkServer(robos, apps);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		server.shutdown();
	}
}
