package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import network.MediaStreaming.IMediaStreamingFrameReceived;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import models.Client;

public class RobotViewController implements Initializable, IMediaStreamingFrameReceived {

	private Client selectedClient;
	private DriveController driveController;
	private CameraController cameraController;
	
	@FXML
	private Slider sldLeftMotor;
	private Timeline leftTimeline;

	@FXML
	private Slider sldRightMotor;
	private Timeline rightTimeline;
	
	private Object timelineLock = new Object();
	
	@FXML
	private ImageView camCanvas;
	@FXML
	private ScrollPane scrollPaneCam;
	
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
		
		camCanvas.setPreserveRatio(true);
		camCanvas.fitWidthProperty().bind(scrollPaneCam.widthProperty());
		camCanvas.fitHeightProperty().bind(scrollPaneCam.heightProperty());
	}
	
	public void setupListeners(Scene s) {
		s.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent t) {
	        	if (t.getText().length() == 0) return;
	            if (t.getText().charAt(0) == 'w') {
	            	if (leftTimeline != null)
	            		leftTimeline.stop();
	            	else
	            		leftTimeline = new Timeline();
	            	createTimeline(leftTimeline, sldLeftMotor, true, timelineLock);
	            	leftTimeline.play();
	            } else if (t.getText().charAt(0) == 's') {
	            	if (leftTimeline != null)
	            		leftTimeline.stop();
	            	else
	            		leftTimeline = new Timeline();
	            	createTimeline(leftTimeline, sldLeftMotor, false, timelineLock);
	            	leftTimeline.play();
	            } else if (t.getText().charAt(0) == 'd') {
	            	sldLeftMotor.valueProperty().set(0);
	            } else if (t.getText().charAt(0) == 'i') {
	            	if (rightTimeline != null)
	            		rightTimeline.stop();
	            	else
	            		rightTimeline = new Timeline();
	            	createTimeline(rightTimeline, sldRightMotor, true, timelineLock);
	            	rightTimeline.play();
	            } else if (t.getText().charAt(0) == 'k') {
	            	if (rightTimeline != null)
	            		rightTimeline.stop();
	            	else
	            		rightTimeline = new Timeline();
	            	createTimeline(rightTimeline, sldRightMotor, false, timelineLock);
	            	rightTimeline.play();
	            } else if (t.getText().charAt(0) == 'j') {
	            	sldRightMotor.valueProperty().set(0);
	            }
	        }
	    });
		
		s.setOnKeyReleased(new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent t) {
	        	if (t.getText().length() == 0) return;
	            if (t.getText().charAt(0) == 'w') {
	            	leftTimeline.stop();
	            } else if (t.getText().charAt(0) == 's') {
	            	leftTimeline.stop();
	            } else if (t.getText().charAt(0) == 'i') {
	            	rightTimeline.stop();
	            } else if (t.getText().charAt(0) == 'k') {
	            	rightTimeline.stop();
	            }
	        }
	    });
	}
	
	private void createTimeline(Timeline tl, Slider slider, boolean up, Object lock) {
		synchronized (lock) {
			tl.getKeyFrames().clear();
			final KeyValue kv = new KeyValue(slider.valueProperty(), 100 * (up ? 1 : -1));
        	final KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
        	tl.getKeyFrames().add(kf);
		}
	}

	public void setRobotView(Client selectedClient,
			DriveController driveController, CameraController cameraController) {
		this.selectedClient = selectedClient;
		this.driveController = driveController;
		this.cameraController = cameraController;
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
		cameraController.turnCameraOn(selectedClient, "@", (13337 + selectedClient.getId()), this);
	}
	
	@FXML
	private void handleCameraOff() {
		cameraController.turnCameraOff(selectedClient);
		camCanvas.setImage(null);
	}

	@Override
	public void frameReceived(Image image, Client client) {
		if (client == selectedClient) {
			camCanvas.setImage(image);
		}
	}

}
