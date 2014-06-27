package graphics;

import java.awt.Dimension;


import routing.NetworkGraph;
import graphics.JCanvas;

public class Printer
{

	private final JCanvas jc;
	private long lastRefresh;

	public Printer(NetworkGraph graph, boolean graphic) {
		if (graphic) {
			jc = new JCanvas();
			jc.setPreferredSize(new Dimension(700,800));
			jc.addDrawable(graph);
			GUIHelper.showOnFrame(jc,"Genetic algorithm");
			lastRefresh = System.currentTimeMillis();
		}
		else 
			jc = null;
	}
	
	public void refresh() 
	{
		jc.repaint();
	}
}

