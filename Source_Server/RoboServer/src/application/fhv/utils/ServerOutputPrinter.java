package utils;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ServerOutputPrinter extends OutputStream {

	// Fields
	protected final TextArea output;

	// Constructor
	public ServerOutputPrinter(TextArea output) {
		this.output = output;
	}

	// Methods
	@Override
	public void write(int i) throws IOException {
		Platform.runLater(() -> output.appendText(String.valueOf((char) i)));
	}
}