package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import models.Client;
import models.ClientFactory;
import network.NetworkServer;
import views.FlashingLabel;

public class RoboTabPageController implements Initializable {

	// Fields
	private final ClientController<Client> roboController;
	private DriveController driveController;
	private CameraController cameraController;
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
	private TableColumn<Client, Number> tcRoboRXCount;

	
	// Robo details
	@FXML
	private TextField tfSend;
	@FXML
	private TextArea tfReceive;
	@FXML
	private TextField tfName;

	@FXML
	private Button btnSend;
	private RobotViewController robotViewController;
	
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
	private void handleViewClick() {
		Client selectedClient = roboController.getSelectedClient();

		if (selectedClient != null) {
			robotViewController.setRobotView(selectedClient, driveController, cameraController);
		}
	}

	@FXML
	private void handleSendClick() {
		Client selectedClient = roboController.getSelectedClient();
		selectedClient.setSendData(tfSend.getText());

		if (selectedClient != null) {
			server.sendToRobo(selectedClient);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the person table with the two columns.
		tcRoboId.setCellValueFactory(cellData -> cellData.getValue().SessionIdProperty());
		tcRoboName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		tcRoboIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());
		tcRoboRXCount.setCellValueFactory(cellData -> cellData.getValue().HeartBeatProperty());
		tvRoboClients.setItems(roboController.getClients());
		tvRoboClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
			@Override
			public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
				roboController.setSelectedClient(newValue);
			}
		});
		
		tcRoboRXCount.setCellFactory(new Callback<TableColumn<Client, Number>, TableCell<Client, Number>>()
        {
            public TableCell<Client, Number> call(TableColumn<Client, Number> column)
            {
                final FlashingLabel label = new FlashingLabel();
                TableCell<Client, Number> cell = new TableCell<Client, Number>()
                {
                    protected void updateItem(Number value, boolean empty)
                    {
                        super.updateItem(value, empty);
                        if (value != null)
                        	label.setText(value.toString());
                    }
                };
                cell.setGraphic(label);
                cell.setStyle("-fx-alignment: CENTER;");
                return cell;
            }
        });

		btnSend.disableProperty().bind(tvRoboClients.getSelectionModel().selectedItemProperty().isNull());
	}

	public ClientController<Client> getRoboController() {
		return roboController;
	}

	public void setServer(NetworkServer server) {
		this.server = server;
		this.driveController = new DriveController(server);
		this.cameraController = new CameraController(server);
	}

	private void clearDetails() {
		tfSend.clear();
	}

	public void setRobotViewController(RobotViewController robotViewController) {
		this.robotViewController = robotViewController;
	}

}
