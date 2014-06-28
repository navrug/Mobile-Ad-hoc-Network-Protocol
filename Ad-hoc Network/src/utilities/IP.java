package utilities;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;


public class IP {
	private final byte[] bytes = new byte[4];
	private boolean isInternetProvider=false;
	private static IP myIP;
	private static IP myDefaultRoute;
	private static String myIface;

	public static IP defineIP()
	{
		Random r = new Random(System.currentTimeMillis());
		myIP = new IP(1, 1, r.nextInt(), r.nextInt());
		myIP.isInternetProvider=false;
		myDefaultRoute = myIP;
		return myIP;
	}
	
	public static IP defineIPFromHost()
	{
		try {
			myIP = new IP(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return myIP;
	}
	
	public static IP defineDefaultRoute()
	{
		myDefaultRoute = myIP;
		return myIP;
	}
	
	public static IP defineDefaultRoute(IP defaultRoute)
	{
		myDefaultRoute = defaultRoute;
		return myDefaultRoute;
	}
	
	public static IP myIP()
	{
		return myIP;
	}
	
	public static IP myDefaultRoute()
	{
		return myDefaultRoute;
	}
	
	public boolean isInternetProvider()
	{
		return isInternetProvider;
	}
	
	public static String myIface()
	{
		return myIface;
	}
	
	public void setInternetProvider(boolean isInternetProvider) {
		this.isInternetProvider = isInternetProvider;
	}

	public IP(byte[] bytes)
	{
		if (bytes.length != 4)
			throw new RuntimeException();
		for (int i = 0; i<4; i++)
			this.bytes[i] = bytes[i];
	}
	
	public IP(byte a, byte b, byte c, byte d)
	{
		bytes[0] = a;
		bytes[1] = b;
		bytes[2] = c;
		bytes[3] = d;	
	}
	
	public IP(int a, int b, int c, int d)
	{
		bytes[0] =(byte) a;
		bytes[1] =(byte) b;
		bytes[2] =(byte) c;
		bytes[3] =(byte) d;	
	}

	public IP(InetAddress address)
	{
		byte[] temp = address.getAddress();
		for (int i = 0; i<4; i++)
			bytes[i] = temp[i];
	}

	@Override
	public boolean equals(Object ip)
	{
		if (ip==null || !(getClass().getName()).equals(
				ip.getClass().getName()))
			return false;
		IP trueIP = (IP) ip;
		boolean result = true;
		for (int i = 0; i<4; i++)
			result &= bytes[i] == trueIP.bytes[i];
		return result;
	}
	
	@Override
	public int hashCode()
	{
		return bytes[3]+256*(bytes[2]+256*(bytes[1]+256*bytes[0]));
	}

	public String toString()
	{
		String[] str = new String[4];
		for (int i = 0; i<4; i++)
			str[i] = Integer.toString((256+bytes[i]) % 256);
		return str[0]+"."+str[1]+"."+str[2]+"."+str[3];
	}

	public byte[] toBytes()
	{
		return bytes;
	}
	
	
}
