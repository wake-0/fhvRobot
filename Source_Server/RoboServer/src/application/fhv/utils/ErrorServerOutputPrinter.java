package utils;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ErrorServerOutputPrinter extends ServerOutputPrinter {

	public ErrorServerOutputPrinter(TextFlow output) {
		super(output);
	}

	@Override
	public void write(int i) throws IOException {
		Text outputText = new Text();
		outputText.setStyle("-fx-fill: RED;-fx-font-weight:normal;");
		outputText.setText(String.valueOf((char) i));

		new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				output.getChildren().addAll(outputText);
				return null;
			}
		}.run();
	}
}