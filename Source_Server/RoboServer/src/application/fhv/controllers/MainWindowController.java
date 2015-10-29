package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import models.Client;
import network.IClientProvider;
import network.NetworkServer;

public class MainWindowController implements Initializable, IClientProvider {

	// fields
	private NetworkServer server;
	private Client selectedClient;
	private ObservableList<Client> observableClients;
	
	// FXML fields
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private TextField tfSend;
	@FXML
	private TextField tfReceive;
	@FXML
	private TextField tfName;
	//@FXML
	//private MediaPlayer mediaPlayer;
	
	// methods
	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
		if (selectedClient != null) {
			lvClients.getItems().remove(lvClients.getSelectionModel().getSelectedItem());
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
			try {
				server.send(selectedClient);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		lvClients.setCellFactory(new Callback<ListView<Client>, ListCell<Client>>() {

			@Override
			public ListCell<Client> call(ListView<Client> p) {

				ListCell<Client> cell = new ListCell<Client>() {

					@Override
					protected void updateItem(Client client, boolean empty) {
						super.updateItem(client, empty);

						if (client != null)
						{
							setText(client.getName());
						} 
//						else if (empty) {
//                            setText("");
//                        }
					}
				};
				return cell;
			}
		});

		observableClients = FXCollections.observableArrayList();
		
		lvClients.setItems(observableClients);
		lvClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
		    @Override
		    public void changed(ObservableValue<? extends Client> observable, Client oldValue, Client newValue) {
		    	if (newValue != null) {
		    		tfName.textProperty().bindBidirectional(newValue.NameProperty());
					tfSend.textProperty().bindBidirectional(newValue.SendDataProperty());
					tfReceive.textProperty().bind(newValue.ReceiveDataProperty());
		    	} 
		    	else if (oldValue != null) 
		    	{
		    		tfSend.textProperty().unbindBidirectional(oldValue.SendDataProperty());
		    		tfName.textProperty().unbindBidirectional(oldValue.NameProperty());
		    		tfReceive.textProperty().unbind();
		    		
		    		tfSend.setText("");
		    		tfReceive.setText("");
		    		tfName.setText("");
		    	}
		    	
		    	selectedClient = newValue;
		    }
		});
		
		this.server = new NetworkServer(this);
		new Thread(this.server).start();
	}

	@Override
	public void addClient(Client client) {
		// Update list
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				observableClients.add(client);
			}
		});	
	}

	@Override
	public void removeClient(Client client) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				observableClients.remove(client);
			}
		});
	}
	
	public Client getClientByIp(String ip) {
		Optional<Client> client = observableClients.stream().filter(c -> c.getIpAddress().equals(ip)).findFirst();
		return client.isPresent() ? client.get() : null;
	}

}
