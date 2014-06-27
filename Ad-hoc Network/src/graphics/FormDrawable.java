package graphics;


import java.awt.*;

public abstract class FormDrawable implements IDrawable
{
	public static final int headerHeight = 100;
	protected final static int figureHeight = 700;
	
	public FormDrawable()
	{
	}
	
	public static int doubleToCoord(double x)
	{
		return ((int)(20+x*(figureHeight-40)));
	}
	
	public abstract void draw(Graphics g) ;
	
}