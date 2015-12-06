package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Client;
import models.ClientFactory;
import network.NetworkServer;

public class AppTabPageController implements Initializable {

	// Fields
	private final ClientController<Client> appController;

	// App table
	@FXML
	private TableView<Client> tvAppClients;
	@FXML
	private TableColumn<Client, Number> tcAppId;
	@FXML
	private TableColumn<Client, String> tcAppName;
	@FXML
	private TableColumn<Client, String> tcAppIp;

	// App details
	@FXML
	private TextField tfSend;
	@FXML
	private TextArea tfReceive;
	@FXML
	private TextField tfName;

	private NetworkServer server;

	public AppTabPageController() {
		appController = new ClientController<>(new ClientFactory());
	}

	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
		Client selectedClient = appController.getSelectedClient();

		if (selectedClient != null) {
			server.DisconnectedAppClient(selectedClient);
			appController.removeClient(selectedClient);
			clearDetails();
		}
	}

	@FXML
	private void handleUpClick() {
		System.out.println("button up clicked.");
	}

	@FXML
	private void handleSendClick() {
		System.out.println("button send clicked.");
		Client selectedClient = appController.getSelectedClient();
		selectedClient.setSendData(tfSend.getText());

		if (selectedClient != null) {
			server.sendToApp(selectedClient);
		}
	}

	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the person table with the two columns.
		tcAppId.setCellValueFactory(cellData -> cellData.getValue().SessionIdProperty());
		tcAppName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		tcAppIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());

		tvAppClients.setItems(appController.getClients());
		tvAppClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
			@Override
			public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
				if (newValue != null) {
					tfName.textProperty().bind(newValue.NameProperty());
					tfReceive.textProperty().bind(newValue.ReceiveDataProperty());
				} else if (oldValue != null) {
					tfName.textProperty().unbind();
					tfReceive.textProperty().unbind();
				}

				appController.setSelectedClient(newValue);
			}
		});
	}

	public ClientController<Client> getAppController() {
		return appController;
	}

	public void setServer(NetworkServer server) {
		this.server = server;
	}

	private void clearDetails() {
		tfSend.clear();
		tfReceive.clear();
		tfName.clear();
	}
}
