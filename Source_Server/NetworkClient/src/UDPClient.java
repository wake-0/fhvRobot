import java.io.*;
import java.net.*;

public class UDPClient implements Runnable {
	java.net.Socket socket;
	
	public static void main(String[] args) {
		UDPClient client = new UDPClient();
		try {
			client.test();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void test() throws IOException {
		String ip = "127.0.0.1"; // localhost
		//String ip = "83.212.127.13";
		int port = 997;
		socket = new java.net.Socket(ip, port); // verbindet
		
		new Thread(this).start();
		while (true) {
			String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
			if (message != null && message != "") {
				String zuSendendeNachricht = message;
				schreibeNachricht(socket, zuSendendeNachricht);
			}
		}
	}

	void schreibeNachricht(java.net.Socket socket, String nachricht) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}

	String leseNachricht(java.net.Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert
																	// bis
																	// Nachricht
																	// empfangen
		String nachricht = new String(buffer, 0, anzahlZeichen);
		return nachricht;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			String empfangeneNachricht;
			try {
				empfangeneNachricht = leseNachricht(socket);
				System.out.println(empfangeneNachricht);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}