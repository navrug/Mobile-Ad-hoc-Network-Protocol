import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {
	

		/*HelloMessage hello = new HelloMessage(InetAddress.getLocalHost());
		hello.display();*/
		new NetworkManager();
}
}
