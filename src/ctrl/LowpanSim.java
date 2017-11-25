/**
 * Class:				LowpanSim.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		25/11/2017
 * Version:				1.0.0
 * 
 * Purpose:				Draw a lowpan mesh network using intuitive user controls.
 * 						Build to allow users to better grasp the goal of the overall project.
 * 						Support various strength lowpan nodes for realism.
 * 
 * Update Log:			v1.0.0
 * 							- null
 */
package ctrl;


//import libraries
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

//import packages
import datatype.IdDispatcher;
import datatype.LowpanNode;
import ui.NetworkView;
import ui.NodeCanvas;




public class LowpanSim implements MouseListener, ActionListener, KeyListener
{
	//declaring static class constants
	public static final String WINDOW_NAME = "6LoWPAN Mesh Network Sim";
	public static final int MAX_X = 1000;
	public static final int MAX_Y = 650;
	public static final int MIN_XY = 10;
	public static final int MIN_RANGE = NodeCanvas.NODE_DIAMETER/2;
	public static final int DEFAULT_RANGE = 100;
	public static final String DEFAULT_NAME = "new_node";
	
	//declaring local instance variables
	private HashSet<LowpanNode> nodes;
	private IdDispatcher dispatch;
	private NetworkView ui;
	
	
	//generic constructor
	public LowpanSim()
	{
		//initialize
		nodes = new HashSet<LowpanNode>();
		dispatch = new IdDispatcher();
		ui = new NetworkView(WINDOW_NAME, nodes, this, this, this);
		ui.enabledKeyInput();
	}
	
	
	//create a new node
	public void addNode(String name, int range, int locX, int locY)
	{
		int id = dispatch.getNextID();
		LowpanNode node = new LowpanNode(id, name, range, locX, locY);
		nodes.add(node);
		computePaths();
	}
	
	
	//remove a node from simulation
	public void removeNode(LowpanNode node)
	{
		for (LowpanNode neighbour : node.getNeighbours())
		{
			neighbour.removeNeighbour(node);
		}
		
		nodes.remove(node);
		dispatch.retireID(node.getId());
		node = null;
		computePaths();
	}
	
	
	@Override
	//respond to update/remove req from interface
	public void actionPerformed(ActionEvent ae) 
	{
		//get active node
		LowpanNode activeNode = ui.getActiveNode();
		
		//determine source
		if (activeNode != null)
		{
			switch(ae.getActionCommand())
			{
				//remove active node
				case (NetworkView.BTN_REMOVE):
					this.removeNode(activeNode);
					ui.setActiveNode(null);
					break;
				
				//update active node
				case (NetworkView.BTN_UPDATE):
					//get inputs
					String name = ui.getInputName();
					Integer range = ui.getInputRange();
					Integer x = ui.getInputX();
					Integer y = ui.getInputY();
					
					//validate inputs
					if (name == null || name.isEmpty())
					{
						break;
					}
					else if (range == null || x == null || y == null)
					{
						break;
					}
					else
					{
						//update node 
						activeNode.setLocation(x, y);
						activeNode.setName(name);
						activeNode.setRange(range);
						
						//update ui
						ui.setActiveNode(activeNode);
					}
					break;
					
				//add new node
				case (NetworkView.BTN_NEW_NODE):
					this.addNode(DEFAULT_NAME, DEFAULT_RANGE, MAX_X/2, MAX_Y/2);
					break;
				
				//remove all nodes
				case (NetworkView.BTN_REMOVE_ALL):
					nodes.clear();
					dispatch.reset();
					break;
			}
			
			//update mesh
			ui.update();
		}
	}
	
	
	@Override
	//respond to clicking in node canvas
	public void mouseClicked(MouseEvent click) 
	{
		ui.enabledKeyInput();			//hacc solution
		int buff = NodeCanvas.NODE_DIAMETER;
		
		//determine if any node selected
		for (LowpanNode node : nodes)
		{
			Point loc = node.getLocation();
			int nodeX = loc.x;
			int nodeY = loc.y;
			
			//check if click was on node
			if (nodeX-buff <= click.getX() && click.getX() <= nodeX+buff &&
				nodeY-buff <= click.getY() && click.getY() <= nodeY+buff)
			{
				ui.setActiveNode(node);
			}
		}
	}
	
	
	@Override
	//keyboard press
	public void keyPressed(KeyEvent ke)
	{
		boolean pathingFlag = true;
		LowpanNode activeNode = ui.getActiveNode();
		if (activeNode != null)
		{
			switch (ke.getKeyCode())
			{
				//move up a step
				case (KeyEvent.VK_UP):
					activeNode.decY();
					ui.update();
					break;
					
				//move down a step
				case (KeyEvent.VK_DOWN):
					activeNode.incY();
					break;
				
				//move left a step
				case (KeyEvent.VK_LEFT):
					activeNode.decX();
					break;
				
				//move right a step
				case (KeyEvent.VK_RIGHT):
					activeNode.incX();
					break;
					
				//increase range
				case (KeyEvent.VK_EQUALS):
					activeNode.incRange();
					break;
				
				//decrease range
				case (KeyEvent.VK_MINUS):
					activeNode.decRange();
					break;
					
				//switch nodes (maybe)
				default:
					pathingFlag = false;
					if (ke.getKeyCode() >= KeyEvent.VK_0 && ke.getKeyCode() <= KeyEvent.VK_9)
					{
						int id = ke.getKeyCode() - 0x30;
						for (LowpanNode node : nodes)
						{
							if (node.getId() == id)
							{
								ui.setActiveNode(node);
								break;
							}
						}
					}
					break;
			}
			ui.update();
		}
		
		//TODO VERY INEFFICENT -- CONSIDER REWRITE
		//update mesh paths for all nodes
		if (pathingFlag)
		{
			computePaths();
		}
	}
	
	
	//compute all node paths
	public void computePaths()
	{
		for (LowpanNode self : nodes)
		{
			for (LowpanNode pair : nodes)
			{
				if (!self.equals(pair))
				{
					//System.out.println(self.getId() + " ... " + pair.getId() + " : " +
					//		self.getLocation().distance(pair.getLocation()));
					if ( self.getLocation().distance(pair.getLocation()) <= self.getRange()+pair.getRange())
					{
						//System.out.println(self.getId() + " --> " + pair.getId());
						self.addNeighbour(pair);
						pair.addNeighbour(self);
					}
					else
					{
						self.removeNeighbour(pair);
					}
				}
			}
		}
	}
	
	
	@Override public void keyReleased(KeyEvent ke) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	@Override public void mousePressed(MouseEvent arg0) {}
	@Override public void mouseReleased(MouseEvent arg0) {}
	@Override public void keyTyped(KeyEvent arg0) {}
	
	
	//main runtime
	public static void main(String[] args)
	{
		LowpanSim sim = new LowpanSim();
		sim.addNode("Isengard TA", 175, 650, 175);
		sim.addNode("Minas Tirith CA", 100, 430, 290);
		sim.addNode("Minas Morgul CA", 100, 310, 170);
		sim.addNode("Rivendell CA", 100, 260, 295);
		sim.addNode("Barad-dur CA", 100, 400, 430);
		sim.addNode("Helm's Deep CA", 100, 550, 550);
	}
}