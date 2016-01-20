package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import models.Client;
import models.Orientation3D;
import network.MediaStreaming.IMediaStreamingFrameReceived;
import views.robot3d.Robot3DViewFactory;
import views.robot3d.Xform;

public class RobotViewController implements Initializable, IMediaStreamingFrameReceived {

    private static final double SCENE_SIZE = 300;
    private static final Color LIGHT_COLOR   = Color.WHITE;

	private Client selectedClient;
	private DriveController driveController;
	private CameraController cameraController;
	
	@FXML
	private Slider sldLeftMotor;
	private Timeline leftTimeline;

	@FXML
	private Slider sldRightMotor;
	private Timeline rightTimeline;
	
	@FXML
	private Pane robot3DView;

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
	private Xform world;
	private Xform robot;
	
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

		createWorld();

		// Init 3D view
        SubScene scene = new SubScene(
                new Group(
                        new AmbientLight(Color.rgb(188, 188, 188)),
                        createPointLight(),
                        world
                ),
                SCENE_SIZE, SCENE_SIZE,
                true,
                SceneAntialiasing.BALANCED
        );
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
//        camera.setTranslateX(90);
//        camera.setTranslateY(90);
        camera.setTranslateZ(-600);
        scene.setCamera(camera);
        robot3DView.getChildren().add(scene);
	}
	
    private PointLight createPointLight() {
        PointLight light = new PointLight(LIGHT_COLOR);
        light.setTranslateX( SCENE_SIZE / 2d);
        light.setTranslateY( SCENE_SIZE / 2d);
        light.setTranslateZ(-SCENE_SIZE);

        return light;
    }

    private void createWorld() {
    	world = new Xform();
    	world.getChildren().add(createAxis());
    	world.getChildren().add((robot = createRobot()));
    	world.rx.setAngle(45);
    }

    private Xform createAxis() {
    	return Robot3DViewFactory.createAxis();
    }

    private Xform createRobot() {
    	return Robot3DViewFactory.createRobot();
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
		if (this.selectedClient != null) {
			this.selectedClient.OrientationProperty().removeListener(listener);
		}
		this.selectedClient = selectedClient;
		this.driveController = driveController;
		this.cameraController = cameraController;
		sldLeftMotor.setValue(0);
		sldRightMotor.setValue(0);
		robotControlledProperty.set(true);
		this.selectedClient.OrientationProperty().addListener(listener);
	}
	
	public void unbindRobotFromControl() {
		robotControlledProperty.set(false);
		setRobotOrientation(new Orientation3D(0,0,0));
		this.selectedClient.OrientationProperty().removeListener(listener);
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

	private ChangeListener<Orientation3D> listener = new ChangeListener<Orientation3D>() {
		@Override
		public void changed(
				ObservableValue<? extends Orientation3D> observable,
				Orientation3D oldValue, Orientation3D newValue) {
			setRobotOrientation(newValue);
		}
	};
	private void setRobotOrientation(Orientation3D newValue) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				robot.reset();
				robot.setRotate(newValue.pitch, newValue.yaw, newValue.roll);
			}
		});
	}
}
