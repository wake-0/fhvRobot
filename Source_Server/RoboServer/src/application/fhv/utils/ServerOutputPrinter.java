package utils;

import java.io.IOException;
import java.io.OutputStream;

import javafx.concurrent.Task;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ServerOutputPrinter extends OutputStream {

	protected final TextFlow output;

	public ServerOutputPrinter(TextFlow output) {
		this.output = output;
	}

	@Override
	public void write(int i) throws IOException {

		Text outputText = new Text();
		outputText.setStyle("-fx-fill: BLACK;-fx-font-weight:normal;");
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