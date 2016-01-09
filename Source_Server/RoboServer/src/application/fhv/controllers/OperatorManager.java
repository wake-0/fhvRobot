package controllers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import communication.commands.Commands;
import communication.flags.Flags;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import models.Client;
import network.IClientController;
import network.communication.Communication;

public class OperatorManager implements Runnable {

	// Fields
	private final IClientController<Client> clientController;
	private final Communication appCommunication;
	private Timer timer;
	
	private String operatorName;

	// 2 min Operator time
	// private static final long OPERATOR_TIME = 2 * 60 * 1000;
	private static final long OPERATOR_TIME = 10 * 1000;

	// Constructor
	public OperatorManager(IClientController<Client> appClients, Communication appCommunication) {
		this.clientController = appClients;
		this.appCommunication = appCommunication;
		this.operatorName = "";
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

		if (oldOperator != null) {
			appCommunication.sendToClient(oldOperator, Flags.REQUEST_FLAG, Commands.ROBO_NOT_STEARING,
					new byte[] { 0 });
		}

		return oldOperator;
	}

	private void selectNextOperator(Client oldOperator) {
		List<Client> clients = clientController.getClients();

		// Select new operator
		int nextIndex = oldOperator == null ? 0 : clients.indexOf(oldOperator) + 1;
		Client nextOperator;

		if (clients == null || clients.size() == 0) {
			nextOperator = null;
			this.operatorName = "";
		} else if (nextIndex < clients.size() && nextIndex > -1) {
			nextOperator = clients.get(nextIndex);
		} else {
			nextOperator = clients.get(0);
		}

		if (nextOperator != null) {
			nextOperator.setIsOperator(true);
			this.operatorName = nextOperator.getName();
			appCommunication.sendToClient(nextOperator, Flags.REQUEST_FLAG, Commands.ROBO_STEARING, new byte[] { 0 });
		}
	}

	private Timer createNewTimer() {

		Timer newTimer = new Timer();

		newTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Client oldOperator = removeOldOperator(clientController.getClients());
				selectNextOperator(oldOperator);
			}
		}, OPERATOR_TIME, OPERATOR_TIME);

		return newTimer;
	}

	@Override
	public void run() {
		this.timer = createNewTimer();

		((ObservableList<Client>) this.clientController.getClients()).addListener(new ListChangeListener<Client>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Client> client) {
				while (client.next()) {
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

			}
		});
	}

	public String getOperatorName() {
		return operatorName;
	}
}
