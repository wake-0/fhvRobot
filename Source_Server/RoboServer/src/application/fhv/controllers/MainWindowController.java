package controllers;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Singleton;

import communication.IClientConfiguration;
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
	
	private Client selectedRoboClient;
	private ObservableList<Client> observableRoboClients;
	
	private Client selectedAppClient;
	private ObservableList<Client> observableAppClients;
	
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
		if (selectedRoboClient != null) {
			removeRoboClient(selectedRoboClient);
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
		observableRoboClients = FXCollections.observableArrayList();
		observableAppClients = FXCollections.observableArrayList();
		
		// Initialize the person table with the two columns.
		tcId.setCellValueFactory(cellData -> cellData.getValue().IdProperty());
        tcName.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
        tcIp.setCellValueFactory(cellData -> cellData.getValue().IpAddressProperty());
		
		tvClients.setItems(observableRoboClients);
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
		    	
		    	selectedRoboClient = newValue;
		    }
		});
		
		try {
			this.server = new NetworkServer(this);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		server.shutdown();
	}
	
	public void addRoboClient(Client client) {
		observableRoboClients.add(client);
	}

	public void removeRoboClient(Client client) {
		observableRoboClients.remove(client);
	}

	@Override
	public List<IClientConfiguration> getRoboClients() {
		return new ArrayList<>(observableRoboClients);
	}

	@Override
	public void addAppClient(Client client) {
		observableAppClients.add(client);
	}

	@Override
	public void removeAppClient(Client client) {
		observableAppClients.remove(client);
	}

	@Override
	public List<IClientConfiguration> getAppClients() {
		return new ArrayList<>(observableAppClients);
	}
}
