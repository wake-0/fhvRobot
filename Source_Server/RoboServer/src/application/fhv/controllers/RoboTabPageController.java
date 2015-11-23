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

public class RoboTabPageController implements Initializable {

	// Fields
	private final ClientController<Client> roboController;
	private NetworkServer server;

	// Robo table
	@FXML
	private TableView<Client> tvRoboClients;
	@FXML
	private TableColumn<Client, Number> tcRoboId;
	@FXML
	private TableColumn<Client, String> tcRoboName;
	@FXML
	private TableColumn<Client, String> tcRoboIp;
	@FXML
	private Client selectedRoboClient;

	// Robo details
	@FXML
	private TextField tfSend;
	@FXML
	private TextArea tfReceive;
	@FXML
	private TextField tfName;

	// Constructor
	public RoboTabPageController() {
		roboController = new ClientController<>(new ClientFactory());
	}

	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
		if (selectedRoboClient != null) {
			roboController.removeClient(selectedRoboClient);
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
		// Initialize the person table with the two columns.
		tcRoboId.setCellValueFactory(cellData -> cellData.getValue().IdProperty());
		tcRoboName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		tcRoboIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());

		tvRoboClients.setItems(roboController.getClients());
		tvRoboClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
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
	}

	public ClientController<Client> getRoboController() {
		return roboController;
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
