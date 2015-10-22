package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import models.Client;
import network.NetworkServer;

public class MainWindowController implements Initializable {

	// fields
	private NetworkServer server;
	private Client selectedClient;
	
	// FXML fields
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private TextField tfSend;
	@FXML
	private TextField tfReceive;
	@FXML
	private TextField tfName;
	
	// methods
	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
		if (selectedClient != null) {
			Client rememberedClient = selectedClient;
			resetSelectedClient();
			server.kill(rememberedClient);
		}
	}

	@FXML
	private void handleUpClick() {
		System.out.println("button up clicked.");
	}

	@FXML
	private void handleSendClick() {
		System.out.println("button send clicked.");
		if (selectedClient != null) {
			server.send(selectedClient);
		}
	}
	
	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<Client> clients = new ArrayList<>();
		ObservableList<Client> observableClients = FXCollections.observableList(clients);

		lvClients.setItems(observableClients);

		lvClients.setCellFactory(new Callback<ListView<Client>, ListCell<Client>>() {

			@Override
			public ListCell<Client> call(ListView<Client> p) {

				ListCell<Client> cell = new ListCell<Client>() {

					@Override
					protected void updateItem(Client t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getName());
						}
					}
				};
				return cell;
			}
		});

		lvClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
		    @Override
		    public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
		    	tfName.textProperty().bindBidirectional(newValue.NameProperty());
				tfSend.textProperty().bindBidirectional(newValue.SendDataProperty());
				tfReceive.textProperty().bind(newValue.ReceiveDataProperty());
				
				newValue.setSendData("data");
				
				selectedClient = newValue;
		    }
		});
		
		this.server = new NetworkServer(observableClients);
		new Thread(this.server).start();
	}
	
	private void resetSelectedClient() {
		tfSend.textProperty().unbind();
		tfReceive.textProperty().unbind();
		tfName.textProperty().unbind();
		selectedClient = null;
	}
}
