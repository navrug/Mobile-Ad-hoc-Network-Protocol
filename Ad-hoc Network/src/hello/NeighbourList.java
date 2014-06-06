package hello;

import java.util.LinkedList;

public class NeighbourList
{
	private LinkedList<String> neighbours;
	
	NeighbourList()
	{
		neighbours = new LinkedList<String>();
	}
	
	public void addNeighbour(String n)
	{
		neighbours.add(n);
		return;
	}
	
	public void helloMessage()
	{
		
	}
}
