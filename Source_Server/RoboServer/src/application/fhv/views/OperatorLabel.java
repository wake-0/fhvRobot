package views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

public class OperatorLabel extends Label {

	private static final String TRUE = "true";
	private static final String FALSE = "false";

	public OperatorLabel() {
		textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> source, String oldValue, String newValue) {
				if (!oldValue.equals(newValue)) {
					if (newValue.equals(TRUE)) {
						setStyle(
								"-fx-text-fill: #33BB33; -fx-stroke: black; -fx-stroke-width: 1; -fx-font-size: 14px;");
						setText("\u265B"); // Black circle would be\u25CF");
					} else if (newValue.equals(FALSE)) {
						setStyle(
								"-fx-text-fill: #AABBAA; -fx-stroke: black; -fx-stroke-width: 1; -fx-font-size: 14px;");
						setText("\u265B"); // Black circle would be\u25CF");
					}
				}
			}
		});
	}

}
