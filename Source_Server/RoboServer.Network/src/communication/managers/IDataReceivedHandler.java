/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: IDataReceivedHandler.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;

import communication.pdu.PDU;

public interface IDataReceivedHandler {

	public boolean handleDataReceived(DatagramPacket packet, PDU pdu, IAnswerHandler sender);
}
