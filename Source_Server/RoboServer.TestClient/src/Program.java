public class Program {

	public static void main(String[] args) {
		UDPClient client = new UDPClient();
		new Thread(client).start();
	}
	
}
