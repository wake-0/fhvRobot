package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {

	  // Define a variable to store the property
    private StringProperty name;
 
    private StringProperty data;
    
    public Client() {
    	name = new SimpleStringProperty();
    	data = new SimpleStringProperty();
    }
    
    // Define a getter for the property's value
    public final String getName() {return name.get();}
 
    // Define a setter for the property's value
    public final void setName(String value){name.set(value);}
 
     // Define a getter for the property itself
    public StringProperty NameProperty() {return name;}
    
    
    // Define a getter for the property's value
    public final String getData() {return data.get();}
 
    // Define a setter for the property's value
    public final void setData(String value){data.set(value);}
 
     // Define a getter for the property itself
    public StringProperty DataProperty() {return data;}
}
