package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client implements Comparable<Client> {

	// fields
    private StringProperty name;
    private StringProperty sendData;
    private StringProperty receiveData;
    private StringProperty ipAddress;
    private IntegerProperty port;
    
    // constructor
    public Client() {
    	name = new SimpleStringProperty();
    	sendData = new SimpleStringProperty();
    	receiveData = new SimpleStringProperty();
    	ipAddress = new SimpleStringProperty();
    	port = new SimpleIntegerProperty();
    	
    	setName("Anonymous");
    }
    
    // getter and setter for the properties
    public final String getName() {return name.get();}
    public final void setName(String value){name.set(value);}
    public StringProperty NameProperty() {return name;}
    
    public final String getSendData() {return sendData.get();}
    public final void setSendData(String value){sendData.set(value);}
    public StringProperty SendDataProperty() {return sendData;}
    
    public final String getReceiveData() {return receiveData.get();}
    public final void setReceiveData(String value){receiveData.set(value);}
    public StringProperty ReceiveDataProperty() {return receiveData;}

    public final String getIpAddress() {return ipAddress.get();}
    public final void setIpAddress(String value){ipAddress.set(value);}
    public StringProperty IpAddressProperty() {return ipAddress;}
    
    public final int getPort() {return port.get();}
    public final void setPort(int value){port.set(value);}
    public IntegerProperty PortProperty() {return port;}
    
	@Override
	public int compareTo(Client o) {
		if (o == null) {return -1;}
		
		return  getIpAddress().compareTo(o.getIpAddress()); 
	}
}
