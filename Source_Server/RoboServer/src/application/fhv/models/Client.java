package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client implements Comparable<Client> {

	// fields
    private StringProperty name;
    private StringProperty sendData;
    private StringProperty receiveData;
    
    // constructor
    public Client() {
    	name = new SimpleStringProperty();
    	sendData = new SimpleStringProperty();
    	receiveData = new SimpleStringProperty();
    	
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

	@Override
	public int compareTo(Client o) {
		return getName().compareTo(o.getName());
	}
}
