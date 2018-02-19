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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

//import packages
import ctrl.LowpanSim;
import datatype.TreeNode;
import ui.SizeReporter;




public class LowpanNode 
{
	//declaring class constants
	public static final int STEP = 30;
	public static final int RANGE_STEP = 5;
	public static final int MIN_RANGE = LowpanSim.MIN_RANGE;
	public static final int MIN_X = LowpanSim.MIN_XY;
	public static final int MIN_Y = LowpanSim.MIN_XY;
	
	//declaring instance variables
	private int id;
	private String name;
	private int range;
	private Point location;
	private HashSet<LowpanNode> neighbours;
	private SizeReporter sizeLimit;
	
	
	//generic constructor
	public LowpanNode(int id, String name, int range, int locX, int locY, SizeReporter sizeLimit)
	{
		this.id = id;
		this.name = name;
		this.sizeLimit = sizeLimit;
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
		else if (locX >= sizeLimit.getCurrentX() - MIN_X)
		{
			location.x = sizeLimit.getCurrentX() - MIN_X;
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
		else if (locY >= sizeLimit.getCurrentY() - MIN_Y)
		{
			location.y = sizeLimit.getCurrentY() - MIN_Y;
		}
		else
		{
			location.y = locY;
		}
	}
	
	
	//confirm location is still valid in new min/max condition
	public void validateLocation()
	{
		Point p = this.getLocation();
		this.setLocation(p.x, p.y);
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
		location.x = (location.x+STEP < sizeLimit.getCurrentX() - MIN_X) ? location.x+STEP : (sizeLimit.getCurrentX() - MIN_X);
	}
	public void decX()
	{
		location.x = (location.x-STEP > MIN_X) ? location.x-STEP : MIN_X;
	}
	
	
	//increment/decrement Y component of location
	public void incY()
	{
		location.y = (location.y+STEP < sizeLimit.getCurrentY() - MIN_Y) ? location.y+STEP : sizeLimit.getCurrentY() - MIN_Y;
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
	
	
	/* convert into tree structure with self as root
	 * guaranteed to only include shortest path(s) to any destination node
	 */
	public TreeNode<LowpanNode> treeify()
	{
		LinkedList<TreeNode<LowpanNode>> curLayer = new LinkedList<TreeNode<LowpanNode>>();
		LinkedList<TreeNode<LowpanNode>> nxtLayer = new LinkedList<TreeNode<LowpanNode>>();
		HashSet<LowpanNode> dump = new HashSet<LowpanNode>();
		TreeNode<LowpanNode> root = new TreeNode<LowpanNode>(null, this);
		
		//add children directly to root node
		for (LowpanNode neighbour : this.neighbours)
		{
			if (neighbour != this)
			{
				TreeNode<LowpanNode> child = root.addChild(neighbour);
				dump.add(neighbour);
				nxtLayer.add(child);
			}
		}
		dump.add(this);
		
		while (!nxtLayer.isEmpty())
		{
			//swap current layer to next layer
			curLayer = nxtLayer;
			nxtLayer = new LinkedList<TreeNode<LowpanNode>>();
			
			//iterate through all neighbor nodes in current layer
			for (TreeNode<LowpanNode> node : curLayer)
			{
				for (LowpanNode linked : node.getSelf().getNeighbours())
				{
					if (!dump.contains(linked))
					{
						TreeNode<LowpanNode> child = node.addChild(linked);
						nxtLayer.add(child);
					}
				}
			}
			
			//add each unique item in nxtLayer
			for (TreeNode<LowpanNode> nxtNode : nxtLayer)
			{
				dump.add(nxtNode.getSelf());
			}
		}
		
		return root;
	}
	
	
	//RPL, single DODAG to destination node if possible
	public ArrayList<LowpanNode> routeRPL(LowpanNode dest, LowpanNode dodag)
	{
		//setup
		ArrayList<LowpanNode> srcToDodag = computeShortestPath(this.treeify(), dodag, null);
		ArrayList<LowpanNode> dodagToDest = computeShortestPath(dodag.treeify(), dest, null);
		
		if (srcToDodag != null && dodagToDest != null)
		{
			dodagToDest.remove(0);
			for (LowpanNode node : dodagToDest)
			{
				srcToDodag.add(node);
			}
			return srcToDodag;
		}
		else
		{
			return null;
		}
	}
	
	
	//route to destination node if the node exists
	public ArrayList<LowpanNode> routeIdeal(LowpanNode dest)
	{
		//setup
		TreeNode<LowpanNode> root = this.treeify();
		ArrayList<LowpanNode> route = computeShortestPath(root, dest, null);
		
		//TODO delete this block
		String s = "";
		if (route != null)
		{
			for (LowpanNode node : route)
			{
				s += node.getId()  + ", ";
			}
		}
		else
		{
			s = "No Routes";
		}
		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ":  LPR:  " +s);
		
		return route;
	}
	
	
	//find the shortest path to any TreeNode in a tree structure
	private ArrayList<LowpanNode> computeShortestPath(TreeNode<LowpanNode> root, LowpanNode dest, ArrayList<LowpanNode> nodeTrace)
	{
		//create a shallow copy of current node path, append root to end
		ArrayList<LowpanNode> path = new ArrayList<LowpanNode>();
		if (nodeTrace != null)
		{
			for (LowpanNode node : nodeTrace)
			{
				path.add(node);
			}
		}
		path.add(root.getSelf());
		
		
		if (root.getSelf().equals(dest))
		{
			return path;
		}
		else
		{
			HashSet<ArrayList<LowpanNode>> possibleRoutes = new HashSet<ArrayList<LowpanNode>>();
			for (TreeNode<LowpanNode>child : root.getChildren())
			{
				ArrayList<LowpanNode> newRoute = computeShortestPath(child, dest, path);
				
				if (newRoute != null)
				{
					possibleRoutes.add(newRoute);
				}
			}
			
			ArrayList<LowpanNode> bestRoute = null;
			for (ArrayList<LowpanNode> route : possibleRoutes)
			{
				if (bestRoute != null)
				{
					if (route.size() == 2)
					{
						return route;
					}
					else if (bestRoute.size() > route.size())
					{
						bestRoute = route;
					}
				}
				else
				{
					bestRoute = route;
				}
			}
			return bestRoute;
		}
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
	
	
	@Override
	//nice printable
	public String toString()
	{
		return "Node " + id;
	}
}
