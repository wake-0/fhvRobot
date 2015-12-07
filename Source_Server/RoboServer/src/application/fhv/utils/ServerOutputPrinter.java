package utils;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ServerOutputPrinter extends OutputStream {

	protected final TextArea output;

	public ServerOutputPrinter(TextArea output) {
		this.output = output;
	}

	@Override
	public void write(int i) throws IOException {
		Platform.runLater(() -> output.appendText(String.valueOf((char) i)));
	}
}