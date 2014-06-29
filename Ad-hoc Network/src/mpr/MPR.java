package mpr;

import java.util.ArrayList;
import java.util.HashSet;

import routing.NetworkGraph;
import utilities.IP;

public class MPR {
	private final ArrayList<IP> myMPR = new ArrayList<IP>();
	private final HashSet<IP> MPRSelectors = new HashSet<IP>();

	
	public ArrayList<IP> computeMPR(NetworkGraph net)
	{
		HashSet<IP> secondsLeft = new HashSet<IP>();
		for (IP neighbor : net.neighbors(IP.myIP()))
			secondsLeft.addAll(net.neighbors(neighbor));
		secondsLeft.remove(IP.myIP());
		int numberOfSeconds;
		int maxSeconds = -1;
		IP nextMPR = null;
		while (!secondsLeft.isEmpty()) {
			for (IP neighbor : net.neighbors(IP.myIP())) {
				numberOfSeconds = 0;
				for (IP second : net.neighbors(neighbor))
					if (secondsLeft.contains(second))
						numberOfSeconds++;
				if (numberOfSeconds > maxSeconds) {
					nextMPR = neighbor;
					maxSeconds = numberOfSeconds;
					for (IP second : net.neighbors(neighbor))
						secondsLeft.remove(second);
				}
			}
			myMPR.add(nextMPR);
		}
		return myMPR;
	}
	
	public boolean MPROf(IP ip)
	{
		return MPRSelectors.contains(ip);
	}
	
	public boolean isMPR(IP ip)
	{
		return myMPR.contains(ip);
	}
}
