package controllers;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.utils.NumberParser;
import models.Client;
import network.NetworkServer;

public class DriveController {

	// Fields
	private final int lowerBound = -100;
	private final int upperBound = 100;

	private final NetworkServer server;

	// Constructors
	public DriveController(NetworkServer server) {
		this.server = server;
	}

	// Methods
	public void driveLeft(Client client, int value) {
		drive(client, Commands.DRIVE_LEFT, value);
	}

	public void driveRight(Client client, int value) {
		drive(client, Commands.DRIVE_RIGHT, value);
	}

	public void driveBoth(Client client, int value) {
		drive(client, Commands.DRIVE_BOTH, value);
	}

	private boolean valueCorrect(int value) {
		return value <= upperBound && value >= lowerBound;
	}

	private void drive(Client client, int command, int value) {
		if (!valueCorrect(value)) {
			return;
		}

		server.sendToRobo(client, Flags.REQUEST_FLAG, command, new byte[] { NumberParser.intToByte(value) });
	}
}
