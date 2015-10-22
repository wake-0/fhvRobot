package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import models.Client;
import network.NetworkClient;

public class MainWindowController implements Initializable {

	private List<Client> clients;

	private Client testClientModel;
	private NetworkClient networkClient;

	@FXML
	private Button btnKill;
	@FXML
	private Button btnUp;
	@FXML
	private Button btnDown;
	@FXML
	private ListView<Client> lvClients;
	@FXML
	private TextField test;

	public MainWindowController() {
		this.testClientModel = new Client();
	}

	@FXML
	private void handleKillClick() {
		System.out.println("button kill clicked.");
	}

	@FXML
	private void handleUpClick() {
		System.out.println("button up clicked.");
	}

	@FXML
	private void handleDownClick() {
		System.out.println("button down clicked.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		clients = new ArrayList<>();
		Client c = new Client();
		c.setName("A");
		clients.add(c);
		c = new Client();
		c.setName("B");
		clients.add(c);
		c = new Client();
		c.setName("C");
		clients.add(c);

		lvClients.setItems(FXCollections.observableList(clients));

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

		// testClientModel.setName("testss");
		test.textProperty().bind(testClientModel.NameProperty());

		this.networkClient = new NetworkClient(testClientModel);
		
		new Thread(this.networkClient).start();

		testClientModel.setName("testss");
	}
}
