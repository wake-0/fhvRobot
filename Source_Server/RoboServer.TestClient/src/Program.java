import com.google.inject.Guice;
import com.google.inject.Injector;

public class Program {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AppInjector());
		UDPClient client = injector.getInstance(UDPClient.class);
		new Thread(client).start();
	}
	
}
