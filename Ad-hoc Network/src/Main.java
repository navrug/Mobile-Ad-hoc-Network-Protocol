import java.net.InetAddress;
import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) {
		InetAddress address = null;
		try {
			 address = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(address.toString());
	}

}
