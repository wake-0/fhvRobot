package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import models.Client;
import models.ClientFactory;
import network.NetworkServer;

public class AppTabPageController implements Initializable {

	// Fields
	private final ClientController<Client> appController;

	private NetworkServer server;

	public AppTabPageController() {
		appController = new ClientController<>(new ClientFactory());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public ClientController<Client> getAppController() {
		return appController;
	}

	public void setServer(NetworkServer server) {
		this.server = server;
	}
}
