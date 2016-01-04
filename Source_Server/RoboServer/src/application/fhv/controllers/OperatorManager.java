package controllers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import models.Client;

public class OperatorManager {

	// Fields
	private final ClientController<Client> clientController;
	private Timer timer;

	// 2 min Operator time
	private static final long OPERATOR_TIME = 2 * 60 * 1000;

	// Constructor
	public OperatorManager(ClientController<Client> appClients) {
		this.clientController = appClients;
		this.timer = createNewTimer();

		this.clientController.getClients().addListener(new ListChangeListener<Client>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Client> client) {
				if (client.wasRemoved()) {

					List<? extends Client> removed = client.getRemoved();

					// Select next operator when the operator was removed
					if (removed.stream().anyMatch(c -> c.getIsOperator())) {

						Client oldOperator = removeOldOperator(removed);
						selectNextOperator(oldOperator);

						// Restart timer
						timer.cancel();
						timer = createNewTimer();
					}
				}
			}
		});
	}

	private Client removeOldOperator(List<? extends Client> clients) {
		Client oldOperator = null;

		// Remove old operator
		for (Client c : clients) {
			if (c.getIsOperator()) {
				oldOperator = c;
				c.setIsOperator(false);
				break;
			}
		}

		return oldOperator;
	}

	private void selectNextOperator(Client oldOperator) {
		ObservableList<Client> clients = clientController.getClients();

		// Select new operator
		int nextIndex = oldOperator == null ? 0 : clients.indexOf(oldOperator) + 1;
		Client nextOperator = null;

		if (nextIndex < clients.size() && nextIndex > -1) {
			nextOperator = clients.get(nextIndex);
		} else {
			nextOperator = clients.get(0);
		}

		nextOperator.setIsOperator(true);
	}

	private Timer createNewTimer() {

		Timer newTimer = new Timer();

		newTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Client oldOperator = removeOldOperator(clientController.getClients());
				selectNextOperator(oldOperator);
			}
		}, OPERATOR_TIME, OPERATOR_TIME);

		return newTimer;
	}
}
