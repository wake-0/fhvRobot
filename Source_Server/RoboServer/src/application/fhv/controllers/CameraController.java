package controllers;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import models.Client;
import network.MediaStreaming;
import network.NetworkServer;

import communication.commands.Commands;
import communication.flags.Flags;

public class CameraController {
	
	private NetworkServer server;
	private Map<Client, MediaStreaming> clientStreamMap;

	// Constructors
	public CameraController(NetworkServer server) {
		this.server = server;
		this.clientStreamMap = new HashMap<>();
	}

	public boolean turnCameraOn(Client client, String host, int port, MediaStreaming.IMediaStreamingFrameReceived frameReceivedCallback) {
		// 1. Send the command
		server.sendToRobo(client, Flags.REQUEST_FLAG, Commands.CAMERA_ON, (host + ":" + port).getBytes());
		
		// 2. Open mediastream on that port
		try {
			MediaStreaming mediaStreaming = new MediaStreaming(port, server.getAppController(), client, frameReceivedCallback);
			new Thread(mediaStreaming).start();
			
			// 3. Map the client to the media stream
			clientStreamMap.put(client, mediaStreaming);
		} catch (SocketException e) {
			return false;
		}
		
		return true;
	}
	
	public void turnCameraOff(Client client) {
		server.sendToRobo(client, Flags.REQUEST_FLAG, Commands.CAMERA_OFF, new byte[0]);
		releaseMediaStreaming(client);
	}

	public void releaseMediaStreaming(Client c) {
		MediaStreaming ms = clientStreamMap.get(c);
		if (ms != null) {
			ms.stop();
		}
	}
}
