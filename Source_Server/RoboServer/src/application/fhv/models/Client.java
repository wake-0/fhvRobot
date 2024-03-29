/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer
 * Filename: Client.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package models;

import java.net.SocketAddress;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client implements Comparable<Client>, IExtendedConfiguration {

	// Fields
	private IntegerProperty id;
	private StringProperty name;
	private StringProperty sendData;
	private StringProperty receiveData;
	private StringProperty ipAddress;
	private IntegerProperty port;
	private IntegerProperty sessionId;
	private IntegerProperty heartBeatCount;
	private SocketAddress socketAddress;
	private BooleanProperty isOperator;
	private ObjectProperty<Orientation3D> orientation;

	// Constructor
	public Client() {
		id = new SimpleIntegerProperty();
		name = new SimpleStringProperty();
		sendData = new SimpleStringProperty();
		receiveData = new SimpleStringProperty();
		ipAddress = new SimpleStringProperty();
		port = new SimpleIntegerProperty();
		sessionId = new SimpleIntegerProperty();
		heartBeatCount = new SimpleIntegerProperty();
		isOperator = new SimpleBooleanProperty(false);
		orientation = new SimpleObjectProperty<>(new Orientation3D(0, 0, 0));
		setName("");
	}

	// Methods
	public final int getId() {
		return id.get();
	}

	public final void setId(int value) {
		id.set(value);
	}

	public IntegerProperty IdProperty() {
		return id;
	}

	public final String getName() {
		return name.get();
	}

	public final void setName(String value) {
		name.set(value);
	}

	public StringProperty NameProperty() {
		return name;
	}

	public final String getSendData() {
		return sendData.get();
	}

	public final void setSendData(String value) {
		sendData.set(value);
	}

	public StringProperty SendDataProperty() {
		return sendData;
	}

	public final String getReceiveData() {
		return receiveData.get();
	}

	public final void setReceiveData(String value) {
		receiveData.set(value);
	}

	public StringProperty ReceiveDataProperty() {
		return receiveData;
	}

	public final String getIpAddress() {
		return ipAddress.get();
	}

	public final void setIpAddress(String value) {
		ipAddress.set(value);
	}

	public StringProperty IpAddressProperty() {
		return ipAddress;
	}

	public final int getPort() {
		return port.get();
	}

	public final void setPort(int value) {
		port.set(value);
	}

	public IntegerProperty PortProperty() {
		return port;
	}

	public final int getSessionId() {
		return sessionId.get();
	}

	public final void setSessionId(int value) {
		sessionId.set(value);
	}

	public IntegerProperty SessionIdProperty() {
		return sessionId;
	}

	public final boolean getIsOperator() {
		return isOperator.get();
	}

	public final void setIsOperator(boolean value) {
		isOperator.set(value);
	}

	public BooleanProperty IsOperatorProperty() {
		return isOperator;
	}

	@Override
	public final int getHeartBeatCount() {
		return heartBeatCount.get();
	}

	public final void setHeartBeatCount(int value) {
		heartBeatCount.set(value);
	}

	public IntegerProperty HeartBeatProperty() {
		return heartBeatCount;
	}

	public ObjectProperty<Orientation3D> OrientationProperty() {
		return orientation;
	}

	public void setOrientation(Orientation3D orientation) {
		this.orientation.set(orientation);
	}

	public Orientation3D getOrientation() {
		return orientation.get();
	}

	@Override
	public void increaseHeartBeatCount() {
		heartBeatCount.set(getHeartBeatCount() + 1);
	}

	@Override
	public void cleanHeartBeatCount() {
		heartBeatCount.set(0);
	}

	@Override
	public int compareTo(Client o) {
		if (o == null) {
			return -1;
		}

		return getIpAddress().compareTo(o.getIpAddress());
	}

	@Override
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public boolean requiresStreamForwarding(Client robotClient) {
		// TODO: We can change this to specific logic to distinguish between
		// clients which get a stream from a specific robot
		return true;
	}
}
