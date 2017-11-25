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
import java.util.HashMap;
import java.util.HashSet;

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
	HashSet<LowpanNode> nodes;
	
	//generic constructor
	public NodeCanvas(HashSet<LowpanNode> nodes)
	{
		super();
		showSignalWells = false;
		showMeshLines = true;
		showNodeIds = true;
		showDistances = false;
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
	
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		//draw all mesh edges as layer 1
		if (showMeshLines)
		{
			for (LowpanNode node : nodes)
			{
				g.setColor(Color.GREEN);
				for (LowpanNode neighbour : node.getNeighbours())
				{
					//draw edge
					g.drawLine(node.getLocation().x, node.getLocation().y, neighbour.getLocation().x, neighbour.getLocation().y);
					
					//label edge
					if(showDistances)
					{
						//TODO
					}
				}
			}
		}
		
		//draw all nodes and wells as layer 2
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
