import java.net.SocketException;
import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException
	{
		PacketManager packet = PacketManager.getInstance();
		packet.run();
	}
}
