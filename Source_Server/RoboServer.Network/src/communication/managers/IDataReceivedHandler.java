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

public interface IDataReceivedHandler<T extends PDU> {

	public boolean handleDataReceived(DatagramPacket packet, T pdu, IAnswerHandler sender);
}
