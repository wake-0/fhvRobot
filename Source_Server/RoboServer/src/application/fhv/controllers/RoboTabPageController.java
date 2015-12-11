package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import communication.utils.NumberParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
	private DriveController driveController;
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

	// Robo details
	@FXML
	private TextField tfSend;
	@FXML
	private TextArea tfReceive;
	@FXML
	private TextField tfName;

	@FXML
	private Button btnDriveLeft;
	@FXML
	private Button btnDriveRight;
	@FXML
	private Button btnDriveBoth;

	@FXML
	private TextField tfDirectionValue;

	// Constructor
	public RoboTabPageController() {
		roboController = new ClientController<>(new ClientFactory());
		roboController.getClients().addListener(new ListChangeListener<Client>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Client> change) {
				while (change.next()) {
					if (change.wasRemoved()) {
						List<? extends Client> removedClients = change.getRemoved();
						for (Client c : removedClients) {
							server.DisconnectedRoboClient(c);
						}
					}
				}
			}
		});
	}

	// Methods
	@FXML
	private void handleKillClick() {
		Client selectedClient = roboController.getSelectedClient();

		if (selectedClient != null) {
			server.DisconnectedRoboClient(selectedClient);
			roboController.removeClient(selectedClient);
			clearDetails();
		}
	}

	@FXML
	private void handleUpClick() {
		System.out.println("button up clicked.");
	}

	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@FXML
	private void handleSendClick() {
		Client selectedClient = roboController.getSelectedClient();
		selectedClient.setSendData(tfSend.getText());

		if (selectedClient != null) {
			server.sendToRobo(selectedClient);
		}
	}

	@FXML
	private void handleDriveClick(ActionEvent event) {
		if (!NumberParser.tryParseInt(tfDirectionValue.getText())) {
			return;
		}

		int value = Integer.parseInt(tfDirectionValue.getText());
		Object source = event.getSource();

		if (source == btnDriveLeft) {
			driveController.driveLeft(value);
		} else if (source == btnDriveRight) {
			driveController.driveRight(value);
		} else if (source == btnDriveBoth) {
			driveController.driveBoth(value);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the person table with the two columns.
		tcRoboId.setCellValueFactory(cellData -> cellData.getValue().SessionIdProperty());
		tcRoboName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		tcRoboIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());

		tvRoboClients.setItems(roboController.getClients());
		tvRoboClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
			@Override
			public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
				if (newValue != null) {
					tfName.textProperty().bind(newValue.NameProperty());
					tfReceive.textProperty().bind(newValue.ReceiveDataProperty());
				} else if (oldValue != null) {
					tfName.textProperty().unbind();
					tfReceive.textProperty().unbind();
				}

				roboController.setSelectedClient(newValue);
			}
		});
	}

	public ClientController<Client> getRoboController() {
		return roboController;
	}

	public void setServer(NetworkServer server) {
		this.server = server;
		this.driveController = new DriveController(server, roboController);
	}

	private void clearDetails() {
		tfSend.clear();
		tfReceive.clear();
		tfName.clear();
	}
}