package views.robot3d;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;

public class Robot3DViewFactory {
	
	public static Xform createRobot() {
		
		Xform robotGroup = new Xform();

		final PhongMaterial wheelMaterial = new PhongMaterial();
		wheelMaterial.setDiffuseColor(Color.ORANGE);
		wheelMaterial.setSpecularColor(Color.BLACK);

		final PhongMaterial greyMaterial = new PhongMaterial();
		greyMaterial.setDiffuseColor(Color.rgb(62, 62, 62));
		greyMaterial.setSpecularColor(Color.WHITE);

		Xform bodyForm = new Xform();
		Cylinder upper = new Cylinder(80, 9);
		upper.setMaterial(greyMaterial);
		Cylinder lower = new Cylinder(70, 3);
		lower.setMaterial(greyMaterial);
		upper.setTranslateY(-30);
		lower.setTranslateY(10);
		bodyForm.getChildren().add(upper);
		bodyForm.getChildren().add(lower);
		
		Xform wheels = new Xform();
		Cylinder left = new Cylinder(30, 30);
		left.setTranslateY(60);
		left.setMaterial(wheelMaterial);
		Cylinder right = new Cylinder(30, 30);
		right.setTranslateY(-60);
		right.setMaterial(wheelMaterial);
		wheels.getChildren().add(left);
		wheels.getChildren().add(right);
		wheels.rz.setAngle(90);
		
		Xform expansions = new Xform();
		Box camera = new Box(40, 40, 40);
		camera.setTranslateY(-35);
		camera.setTranslateZ(50);
		camera.setMaterial(new PhongMaterial(Color.BLUE));
		expansions.getChildren().add(camera);
		
		robotGroup.getChildren().add(bodyForm);
		robotGroup.getChildren().add(wheels);
		robotGroup.getChildren().add(expansions);
		return robotGroup;
	}

	public static Xform createAxis() {
		Xform axisGroup = new Xform();
		final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(240.0, 1, 1);
        final Box yAxis = new Box(1, 240.0, 1);
        final Box zAxis = new Box(1, 1, 240.0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        return axisGroup;
    }

}
