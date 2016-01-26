package controllers;

import communication.commands.Commands;
import communication.flags.Flags;
import communication.utils.NumberParser;
import models.Client;
import network.NetworkServer;

public class LightController {

	private final NetworkServer server;

	// Constructors
	public LightController(NetworkServer server) {
		this.server = server;
	}

	// Methods
	public void sendTriggerLED(Client client) {
		server.sendToRobo(client, Flags.REQUEST_FLAG, Commands.TRIGGER_LED, new byte[0]);
	}

}
