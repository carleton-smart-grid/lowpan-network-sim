/**
 * Class:				LowpanNode.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		25/11/2017
 * Version:				1.0.0
 * 
 * Purpose:				Represent a generic 6lowpan networked node.
 * 
 * Update Log:			v1.0.0
 * 							- null
 */
package datatype;


//import libraries
import java.awt.Point;
import java.util.HashSet;

//import packages
import ctrl.LowpanSim;




public class LowpanNode 
{
	//declaring class constants
	public static final int STEP = 30;
	public static final int RANGE_STEP = 5;
	public static final int MIN_RANGE = LowpanSim.MIN_RANGE;
	public static final int MAX_X = LowpanSim.MAX_X;
	public static final int MAX_Y = LowpanSim.MAX_Y;
	public static final int MIN_X = LowpanSim.MIN_XY;
	public static final int MIN_Y = LowpanSim.MIN_XY;
	
	//declaring instance variables
	private int id;
	private String name;
	private int range;
	private Point location;
	private HashSet<LowpanNode> neighbours;
	
	
	//generic constructor
	public LowpanNode(int id, String name, int range, int locX, int locY)
	{
		this.id = id;
		this.name = name;
		setRange(range);
		
		location = new Point();
		setLocation(locX, locY);
		
		neighbours = new HashSet<LowpanNode>();
	}
	
	
	//generic getters
	public int getRange()
	{
		return range;
	}
	public String getName()
	{
		return name;
	}
	public Point getLocation()
	{
		return location;
	}
	public int getId()
	{
		return id;
	}
	public HashSet<LowpanNode> getNeighbours()
	{
		return neighbours;
	}
	
	
	//generic setters
	public void setRange(int range)
	{
		this.range = (range >= 0) ? range : MIN_RANGE;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void setLocation(int locX, int locY)
	{
		//set X
		if (locX <= MIN_X)
		{
			location.x = MIN_X;
		}
		else if (locX >= MAX_X)
		{
			location.x = MAX_X;
		}
		else
		{
			location.x = locX;
		}
		
		//set Y
		if (locY <= MIN_Y)
		{
			location.y = MIN_Y;
		}
		else if (locY >= MAX_Y)
		{
			location.y = MAX_Y;
		}
		else
		{
			location.y = locY;
		}
	}
	
	
	//add/remove a neighbour node
	public void addNeighbour(LowpanNode node)
	{
		neighbours.add(node);
	}
	public void removeNeighbour(LowpanNode node)
	{
		neighbours.remove(node);
	}
	
	
	//increment/decrement X component of location
	public void incX()
	{
		location.x = (location.x+STEP < MAX_X) ? location.x+STEP : MAX_X;
	}
	public void decX()
	{
		location.x = (location.x-STEP > MIN_X) ? location.x-STEP : MIN_X;
	}
	
	
	//increment/decrement Y component of location
	public void incY()
	{
		location.y = (location.y+STEP < MAX_Y) ? location.y+STEP : MAX_Y;
	}
	public void decY()
	{
		location.y = (location.y-STEP > MIN_Y) ? location.y-STEP : MIN_Y;
	}
	
	
	//increment/decrement range
	public void incRange()
	{
		range += RANGE_STEP;
	}
	public void decRange()
	{
		range = (range > MIN_RANGE+RANGE_STEP) ? range-RANGE_STEP : MIN_RANGE;
	}
	
	
	@Override
	//generic equals
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof LowpanNode)
		{
			LowpanNode otherNode = (LowpanNode)obj;
			return (name.equals(otherNode.name) && id == otherNode.id && range == otherNode.range && location.equals(otherNode.location));
		}
		else
		{
			return false;
		}
	}
	
}
