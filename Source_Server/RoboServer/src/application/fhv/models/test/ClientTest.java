package models.test;

import static org.junit.Assert.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import models.Client;
import models.Orientation3D;

import org.junit.Test;

public class ClientTest {

	private boolean changeFlag = false;
	
	@Test
	public void testOrientation() {
		Client client = new Client();
		client.setOrientation(new Orientation3D(0, 0, 0));
		
		assertFalse(changeFlag);
		client.OrientationProperty().addListener(new ChangeListener<Orientation3D>() {
			@Override
			public void changed(
					ObservableValue<? extends Orientation3D> observable,
					Orientation3D oldValue, Orientation3D newValue) {
				
				changeFlag = true;
				
			}
		});
		
		client.setOrientation(new Orientation3D(1, 1, 1));
		assertTrue(changeFlag);
	}

}
