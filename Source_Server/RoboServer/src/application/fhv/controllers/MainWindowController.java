package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.Client;
import network.IClientProvider;
import network.NetworkServer;

@Singleton
public class MainWindowController implements Initializable, IClientProvider {

	// fields
	private NetworkServer server;
	private Client selectedClient;
	private ObservableList<Client> observableClients;
	
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
	//@FXML
	//private MediaPlayer mediaPlayer;
	
	// methods
	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
		if (selectedClient != null) {
			tvClients.getItems().remove(tvClients.getSelectionModel().getSelectedItem());
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
		observableClients = FXCollections.observableArrayList();
		
		// Initialize the person table with the two columns.
		tcId.setCellValueFactory(cellData -> cellData.getValue().IdProperty());
        tcName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
        tcIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());
		
		tvClients.setItems(observableClients);
		tvClients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Client>() {
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
		    	}
		    	
		    	selectedClient = newValue;
		    }
		});
		
		Injector injector = Guice.createInjector(new AppInjector(this));
		this.server = injector.getInstance(NetworkServer.class);
		new Thread(this.server).start();
	}

	@Override
	public void addClient(Client client) {
		observableClients.add(client);
	}

	@Override
	public void removeClient(Client client) {
		observableClients.remove(client);
	}
	
	public Client getClientByIp(String ip) {
		Optional<Client> client = observableClients.stream().filter(c -> c.getIpAddress().equals(ip)).findFirst();
		return client.isPresent() ? client.get() : null;
	}

	public void shutdown() {
		server.shutdown();
	}

}
