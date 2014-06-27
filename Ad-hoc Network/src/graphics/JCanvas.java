package graphics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;


public class JCanvas extends JPanel
{
	private static final long serialVersionUID = 1L;
	public List<IDrawable> drawables = new LinkedList<IDrawable>();

	public void paint(Graphics g)
	{	
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,FormDrawable.figureHeight,
				FormDrawable.headerHeight + FormDrawable.figureHeight);
		g.setColor(c);

		for (Iterator<IDrawable> iter = drawables.iterator(); iter.hasNext();) {
			IDrawable d = (IDrawable) iter.next();
			d.draw(g);	
		}
	}

	public void addDrawable(IDrawable d)
	{
		drawables.add(d);
		repaint();
	}

	public void removeDrawable(IDrawable d)
	{
		drawables.remove(d);
		repaint();
	}
	
	public void clear()
	{
		drawables.clear();
		repaint();
	}
	
}