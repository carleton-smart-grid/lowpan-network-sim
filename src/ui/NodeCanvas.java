/**
 * Class:				NodeCanvas.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		25/11/2017
 * Version:				1.0.0
 * 
 * Purpose:				Draw a system of nodes, including signal wells and mesh links.
 * 
 * Update Log:			v1.0.0
 * 							- null
 */
package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ctrl.LowpanSim;
import datatype.LowpanNode;

public class NodeCanvas extends JPanel 
{
	//declaring static class constants
	private static final double SIM_X = LowpanSim.MAX_X;
	private static final double SIM_Y = LowpanSim.MAX_Y;
	public static final int NODE_DIAMETER = 20;
	
	
	//declaring local instance variables
	private boolean showSignalWells;
	private boolean showMeshLines;
	private boolean showNodeIds;
	private boolean showDistances;
	private boolean showIdealRouting;
	private boolean showRplRouting;
	private LowpanNode src, dest;
	HashSet<LowpanNode> nodes;
	
	//generic constructor
	public NodeCanvas(HashSet<LowpanNode> nodes)
	{
		super();
		showSignalWells = false;
		showMeshLines = true;
		showNodeIds = true;
		showDistances = false;
		showIdealRouting = false;
		showRplRouting = false;
		src = null;
		dest = null;
		this.nodes = nodes;
	}
	
	
	//generic setters
	public void setSignalWells(boolean flag)
	{
		showSignalWells = flag;
	}
	public void setMeshLines(boolean flag)
	{
		showMeshLines = flag;
	}
	public void setDistances(boolean flag)
	{
		showDistances = flag;
	}
	public void setNodeIds(boolean flag)
	{
		showNodeIds = flag;
	}
	public void setRplRouting(boolean flag)
	{
		showRplRouting = flag;
	}
	public void setIdealRouting(boolean flag)
	{
		showIdealRouting = flag;
	}
	public void setRoutingNodes(LowpanNode src, LowpanNode dest)
	{
		this.src = src;
		this.dest = dest;
	}
	
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		//draw all mesh edges as layer 1
		if (showMeshLines)
		{
			for (LowpanNode node : nodes)
			{
				for (LowpanNode neighbour : node.getNeighbours())
				{
					//draw edge
					g.setColor(Color.GREEN);
					g.drawLine(node.getLocation().x, node.getLocation().y, neighbour.getLocation().x, neighbour.getLocation().y);
					
					//label edge
					if(showDistances)
					{
						g.setColor(Color.BLACK);
						
						String dist = String.format("%.02f", node.getLocation().distance(neighbour.getLocation()));
						int x = ((node.getLocation().x + neighbour.getLocation().x) / 2) + 10;
						int y = ((node.getLocation().y + neighbour.getLocation().y) / 2) + 10;
						g.drawString(dist, x, y);
					}
				}
			}
		}
		
		//draw all routing as layer 2
		if ((src != null && dest != null))
		{
			if (showRplRouting)
			{
				g.setColor(Color.MAGENTA);
				//TODO RPL ROUTE
			}
			
			if (showIdealRouting)
			{
				g.setColor(Color.BLUE);
				ArrayList<LowpanNode> path = src.routeTo(dest);
				if (path != null)
				{
					if (path.size() > 2)
					{
						for (int i=0; i<path.size()-1; i++)
						{
							LowpanNode cur = path.get(i);
							LowpanNode nxt = path.get(i+1);
							
							g.drawLine(cur.getLocation().x, cur.getLocation().y, nxt.getLocation().x, nxt.getLocation().y);
						}
					}
					else if (path.size() == 2)
					{
						LowpanNode cur = path.get(0);
						LowpanNode nxt = path.get(1);
						
						g.drawLine(cur.getLocation().x, cur.getLocation().y, nxt.getLocation().x, nxt.getLocation().y);
					}
					else
					{
						//TODO throw error, unexpected routing result
					}
				}
			}
		}
		else
		{
			System.out.println("NULL");
		}
		
		//draw all nodes and wells as layer 3
		for (LowpanNode node : nodes)
		{
			//draw node
			Point loc = node.getLocation();
			int centroidX = loc.x - NODE_DIAMETER/2;
			int centroidY = loc.y - NODE_DIAMETER/2;
			g.setColor(Color.BLACK);
			g.fillOval(centroidX, centroidY, NODE_DIAMETER, NODE_DIAMETER);
			
			//draw signal well
			if (showSignalWells)
			{
				int wellDiameter = 2*node.getRange();
				int wellCentroidX = loc.x - wellDiameter/2;
				int wellCentroidY = loc.y - wellDiameter/2;
				
				g.setColor(Color.RED);
				g.drawOval(wellCentroidX, wellCentroidY, wellDiameter, wellDiameter);
			}
			
			//label node
			if (showNodeIds)
			{
				String id = ""+node.getId();
				int charOffset = id.length()*4;
				g.setColor(Color.BLACK);
				g.drawString(id, loc.x-charOffset, loc.y+NODE_DIAMETER);
			}
		}
	}
}
