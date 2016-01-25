package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import communication.commands.Commands;
import controllers.ClientController.ICommandListener;
import javafx.application.Platform;
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
import javafx.scene.control.ToggleButton;
import javafx.util.Callback;
import models.Client;
import models.ClientFactory;
import network.NetworkServer;
import views.FlashingLabel;
import views.OperatorLabel;

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
	@FXML
	private TableColumn<Client, Number> tcAppRXCount;
	@FXML
	private TableColumn<Client, Boolean> tcAppIsOperator;

	@FXML
	private TextArea tfReceive;

	// App details
	@FXML
	private TextField tfSend;
	@FXML
	private Button btnSend;
	@FXML
	private Button btnSetOperator;
	@FXML
	private Button btnReleaseOperator;
	@FXML
	private ToggleButton btnAutomodeOperator;
	@FXML
	private Button btnKill;

	private NetworkServer server;

	// Constructor
	public AppTabPageController() {
		appController = new ClientController<>(new ClientFactory());
		appController.getClients().addListener(new ListChangeListener<Client>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Client> change) {
				while (change.next()) {
					boolean autoMode = btnAutomodeOperator != null && btnAutomodeOperator.isSelected();

					if (change.wasRemoved()) {
						List<? extends Client> removedClients = change.getRemoved();
						for (Client c : removedClients) {
							server.DisconnectedAppClient(c);
						}

						if (autoMode) {
							autoUpdateOperator(appController.getClients());
						}
					} else if (change.wasAdded()) {
						// set next operator
						if (autoMode) {
							autoUpdateOperator(change.getAddedSubList());
						}
					}
				}
			}
		});

		appController.addCommandListener(new ICommandListener<Client>() {
			@Override
			public void commandReceived(Client client, int command, byte[] payload) {
				Platform.runLater(() -> {
					tfReceive.appendText("[<-" + client.getName() + "] " + new String(payload) + "\n");
				});
			}
		}, Commands.GENERAL_MESSAGE);
	}

	private void autoUpdateOperator(List<? extends Client> addedClients) {
		// check already a operator selected
		if (!appController.getOperators().isEmpty()) {
			return;
		}

		if (!addedClients.isEmpty()) {
			addedClients.get(0).setIsOperator(true);
		}
	}

	// Methods
	@FXML
	private void handleKillClick() {
		Client selectedClient = appController.getSelectedClient();

		if (selectedClient != null) {
			server.DisconnectedAppClient(selectedClient);
			appController.removeClient(selectedClient);
			clearDetails();
		}
	}

	@FXML
	private void handleSetOperatorClick() {
		// Only allow one operator
		appController.releaseAllOperators();
		appController.setOperator(appController.getSelectedClient());
	}

	@FXML
	private void handleReleaseOperatorClick() {
		appController.releaseOperator(appController.getSelectedClient());
	}

	@FXML
	private void handleSendClick() {
		Client selectedClient = appController.getSelectedClient();
		selectedClient.setSendData(tfSend.getText());

		tfReceive.appendText("[->" + selectedClient.getName() + "] " + tfSend.getText() + "\n");

		if (selectedClient != null) {
			server.sendToApp(selectedClient);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize the person table with the two columns.
		tcAppId.setCellValueFactory(cellData -> cellData.getValue().SessionIdProperty());
		tcAppName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		tcAppIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());
		tcAppRXCount.setCellValueFactory(cellData -> cellData.getValue().HeartBeatProperty());
		tcAppIsOperator.setCellValueFactory(cellData -> cellData.getValue().IsOperatorProperty());

		tfReceive.appendText("Chat Window:\n");

		tvAppClients.setItems(appController.getClients());
		tvAppClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
			@Override
			public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
				appController.setSelectedClient(newValue);
			}
		});

		tcAppRXCount.setCellFactory(new Callback<TableColumn<Client, Number>, TableCell<Client, Number>>() {
			public TableCell<Client, Number> call(TableColumn<Client, Number> column) {
				final FlashingLabel label = new FlashingLabel();
				TableCell<Client, Number> cell = new TableCell<Client, Number>() {
					protected void updateItem(Number value, boolean empty) {
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

		tcAppIsOperator.setCellFactory(new Callback<TableColumn<Client, Boolean>, TableCell<Client, Boolean>>() {

			public TableCell<Client, Boolean> call(TableColumn<Client, Boolean> column) {
				final OperatorLabel label = new OperatorLabel();

				TableCell<Client, Boolean> cell = new TableCell<Client, Boolean>() {
					@Override
					protected void updateItem(Boolean item, boolean empty) {
						super.updateItem(item, empty);

						if (item != null) {
							label.setText(item.toString());
						}

					}
				};
				cell.setGraphic(label);
				cell.setStyle("-fx-alignment: CENTER;");
				return cell;
			}
		});

		btnSend.disableProperty().bind(tvAppClients.getSelectionModel().selectedItemProperty().isNull());
		btnKill.disableProperty().bind(tvAppClients.getSelectionModel().selectedItemProperty().isNull());
		btnSetOperator.disableProperty().bind(tvAppClients.getSelectionModel().selectedItemProperty().isNull());
		btnReleaseOperator.disableProperty().bind(tvAppClients.getSelectionModel().selectedItemProperty().isNull());
	}

	public ClientController<Client> getAppController() {
		return appController;
	}

	public void setServer(NetworkServer server) {
		this.server = server;
	}

	private void clearDetails() {
		tfSend.clear();
	}

}
