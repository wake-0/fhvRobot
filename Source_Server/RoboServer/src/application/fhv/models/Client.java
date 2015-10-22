package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {

	// fields
    private StringProperty name;
    private StringProperty data;
    
    // constructor
    public Client() {
    	name = new SimpleStringProperty();
    	data = new SimpleStringProperty();
    }
    
    // getter and setter for the properties
    public final String getName() {return name.get();}
    public final void setName(String value){name.set(value);}
    public StringProperty NameProperty() {return name;}
    
    public final String getData() {return data.get();}
    public final void setData(String value){data.set(value);}
    public StringProperty DataProperty() {return data;}
}
