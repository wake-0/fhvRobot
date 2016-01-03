package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import models.Client;
import models.ClientFactory;
import network.NetworkServer;
import views.FlashingLabel;
import communication.utils.NumberParser;

public class RobotViewController implements Initializable {

	private Client selectedClient;
	private DriveController driveController;
	
	@FXML
	private Slider sldLeftMotor;

	@FXML
	private Slider sldRightMotor;
	
	@FXML
	private Node camCanvas;
	
	@FXML
	private Button btnCameraOn;
	@FXML
	private Button btnCameraOff;
	
	private BooleanProperty robotControlledProperty;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		robotControlledProperty = new SimpleBooleanProperty();
		sldLeftMotor.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
            		if (new_val != null && driveController != null) {
                        driveController.driveLeft(selectedClient, new_val.intValue());
            		}
                }
            });
		sldRightMotor.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
            		if (new_val != null && driveController != null) {
                        driveController.driveRight(selectedClient, new_val.intValue());
            		}
                }
            });
		
		sldLeftMotor.disableProperty().bind(robotControlledProperty.not());
		sldRightMotor.disableProperty().bind(robotControlledProperty.not());
		btnCameraOff.disableProperty().bind(robotControlledProperty.not());
		btnCameraOn.disableProperty().bind(robotControlledProperty.not());
	}

	public void setRobotView(Client selectedClient,
			DriveController driveController) {
		this.selectedClient = selectedClient;
		this.driveController = driveController;
		sldLeftMotor.setValue(0);
		sldRightMotor.setValue(0);
		robotControlledProperty.set(true);
	}
	
	public void unbindRobotFromControl() {
		robotControlledProperty.set(false);
		this.selectedClient = null;
		this.driveController = null;
	}

	@FXML
	private void handleCameraOn() {
	}
	
	@FXML
	private void handleCameraOff() {
	}
}
