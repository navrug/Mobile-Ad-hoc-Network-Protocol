package utilities;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Random;


public class IP {
	private final byte[] bytes = new byte[4];
	private boolean isInternetProvider=false;
	private static IP myIP;
	private static IP myDefaultRoute;
	private static String myIface;

	public static void defineIP(String configPath)
	{
		 Properties properties=new Properties();
		 try {
		 FileInputStream in =new FileInputStream(configPath);
		 properties.load(in);
		 in.close();
		 } catch (IOException e) {
		 System.out.println("Unable to load config file.");
		 }
		
		Random r = new Random(System.currentTimeMillis());
		IP.myIP = new IP(1, 1, r.nextInt(), r.nextInt());
		String myIPAlea = IP.myIP.toString();
		try {
			IP.myIP = new IP(InetAddress.getByName(properties.getProperty("IP", myIPAlea)));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		IP.myIP.isInternetProvider=(properties.getProperty("IsInternetProvider", "0")!="0");
		try {
			IP.myDefaultRoute = new IP(InetAddress.getByName(properties.getProperty("DefaultRoute", IP.myIP.toString())));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static void defineIP() {
		defineIP("config.ini");
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
