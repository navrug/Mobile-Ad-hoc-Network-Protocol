package graphics;

import java.awt.Dimension;



import routing.NetworkGraph;
import routing.RoutingTable;
import graphics.JCanvas;

public class Printer
{

	private final JCanvas jc;
	private long lastRefresh;

	public Printer(RoutingTable table, boolean graphic) {
		if (graphic) {
			jc = new JCanvas();
			jc.setPreferredSize(new Dimension(700,800));
			jc.addDrawable(table);
			GUIHelper.showOnFrame(jc,"Network");
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

