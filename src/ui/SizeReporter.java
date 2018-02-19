package ui;

import java.awt.Dimension;

public interface SizeReporter 
{
	//get the current dimensions (xy pair) of realization
	public Dimension getCurrentSize();
	
	//get the current X value of the realization
	public int getCurrentX();
	
	//get the current Y value of the realization
	public int getCurrentY();
}
