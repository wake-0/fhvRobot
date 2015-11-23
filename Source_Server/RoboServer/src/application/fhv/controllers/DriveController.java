package controllers;

import communication.commands.Commands;
import communication.utils.NumberParser;
import models.Client;
import network.NetworkServer;

public class DriveController {

	// Fields
	private final int lowerBound = -100;
	private final int upperBound = 100;

	private final NetworkServer server;
	private final ClientController<Client> roboController;

	// Constructors
	public DriveController(NetworkServer server, ClientController<Client> roboController) {
		this.server = server;
		this.roboController = roboController;
	}

	// Methods
	public void driveLeft(int value) {
		drive(Commands.DRIVE_LEFT, value);
	}

	public void driveRight(int value) {
		drive(Commands.DRIVE_RIGHT, value);
	}

	public void driveBoth(int value) {
		drive(Commands.DRIVE_BOTH, value);
	}

	private boolean valueCorrect(int value) {
		return value <= upperBound && value >= lowerBound;
	}

	private void drive(int command, int value) {
		if (!valueCorrect(value)) {
			return;
		}

		Client client = roboController.getSelectedClient();
		server.sendToRobo(client, command, new byte[] { NumberParser.intToByte(value) });
	}
}
