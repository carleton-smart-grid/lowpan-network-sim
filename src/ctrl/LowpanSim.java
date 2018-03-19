/**
 * Class:				LowpanSim.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		12/02/2017
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


import java.awt.Dimension;
//import libraries
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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




public class LowpanSim implements MouseListener, ActionListener, KeyListener, ComponentListener
{
	//declaring static class constants
	public static final String WINDOW_NAME = "6LoWPAN Mesh Network Sim";
	public static final int MIN_XY = 10;
	public static final int MIN_RANGE = NodeCanvas.NODE_DIAMETER/2;
	public static final int DEFAULT_RANGE = 100;
	public static final String DEFAULT_NAME = "new_node";
	
	//declaring local instance variables
	private HashSet<LowpanNode> nodes;
	private IdDispatcher dispatch;
	private NetworkView ui;
	private boolean radioType;		// T => easy || F => real
	
	
	//generic constructor
	public LowpanSim()
	{
		this(false);
	}
	//base constructor
	public LowpanSim(boolean fullscreen)
	{
		//initialize
		nodes = new HashSet<LowpanNode>();
		dispatch = new IdDispatcher();
		ui = new NetworkView(WINDOW_NAME, nodes, fullscreen, this, this, this, this);
		ui.enabledKeyInput();
		radioType = true;
	}
	
	
	//create a new node
	public void addNode(String name, int range, int locX, int locY)
	{
		int id = dispatch.getNextID();
		LowpanNode node = new LowpanNode(id, name, range, locX, locY, ui);
		nodes.add(node);
		
		ui.updateRoutingSelectors(nodes);
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
		
		ui.updateRoutingSelectors(nodes);
		computePaths();
	}
	
	
	//remove all nodes from simulator
	public void removeAllNodes()
	{
		nodes.clear();
	}
	
	
	//get all nodes in simulation
	public HashSet<LowpanNode> getNodes()
	{
		return nodes;
	}
	
	@Override
	//respond to update/remove req from interface
	public void actionPerformed(ActionEvent ae) 
	{
		//get active node
		LowpanNode activeNode = ui.getActiveNode();
		String cmd = ae.getActionCommand();

		//source is altering radio type
		if (cmd.equals(NetworkView.RADIO_TYPE_A))
		{
			radioType = true;
			this.computePaths();
			ui.update();
		}
		else if (cmd.equals(NetworkView.RADIO_TYPE_B))
		{
			radioType = false;
			this.computePaths();
			ui.update();
		}
		
		//change to linear preset
		else if (cmd.equals(NetworkView.PRESETS[0]))
		{
			dispatch.reset();
			this.removeAllNodes();
			this.addNode("Mercury", 100, 85, 320);
			this.addNode("Venus", 100, 265, 320);
			this.addNode("Earth", 100, 445, 320);
			this.addNode("Mars", 100, 625, 320);
			this.addNode("Ceres", 100, 805, 320);
			ui.update();
		}
		//change to tree preset
		else if (cmd.equals(NetworkView.PRESETS[1]))
		{
			dispatch.reset();
			this.removeAllNodes();
			this.addNode("Clarke", 100, 505, 50);
			this.addNode("Asimov", 100, 325, 110);
			this.addNode("Heinlein", 100, 685, 110);
			this.addNode("Kennedy", 100, 205, 260);
			this.addNode("Banks", 100, 445, 260);
			this.addNode("Adams", 100, 685, 260);
			this.addNode("Capek", 75, 115, 350);
			this.addNode("Boulle", 70, 295, 350);
			this.addNode("Cherryh", 80, 385, 470);
			this.addNode("Herbert", 65, 235, 470);
			ui.update();
		}
		//change to sparse preset
		else if (cmd.equals(NetworkView.PRESETS[2]))
		{
			dispatch.reset();
			this.removeAllNodes();
			this.addNode("Isengard TA", 175, 650, 175);
			this.addNode("Minas Tirith CA", 100, 430, 290);
			this.addNode("Minas Morgul CA", 100, 310, 170);
			this.addNode("Rivendell CA", 100, 260, 295);
			this.addNode("Barad-dur CA", 100, 400, 430);
			this.addNode("Helm's Deep CA", 100, 550, 550);
			ui.update();
		}
		//change to cluster preset
		else if (cmd.equals(NetworkView.PRESETS[3]))
		{
			dispatch.reset();
			this.removeAllNodes();
			//TODO add something
			ui.update();
		}
		//change to matrix preset
		else if (cmd.equals(NetworkView.PRESETS[4]))
		{
			dispatch.reset();
			this.removeAllNodes();
			this.addNode("Julius", 100, 235, 110);
			this.addNode("Augustus", 100, 415, 110);
			this.addNode("Tiberius", 100, 595, 110);
			this.addNode("Caligula", 100, 325, 200);
			this.addNode("Claudius", 100, 505, 200);
			this.addNode("Nero", 100, 235, 290);
			this.addNode("Titus", 100, 415, 290);
			this.addNode("Marcus Aurelius", 100, 595, 290);
			this.addNode("Commodus", 100, 325, 380);
			this.addNode("Septimius Severus", 100, 505, 380);
			this.addNode("Constantine I", 100, 235, 470);
			this.addNode("Valerian", 100, 415, 470);
			this.addNode("Pertinax", 100, 595, 470);
			ui.update();
		}
		
		//source is alternating node information
		else
		{
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
						Dimension cs = ui.getCanvasSize();
						this.addNode(DEFAULT_NAME, DEFAULT_RANGE, cs.height/2, cs.width/2);
						break;
					
					//remove all nodes
					case (NetworkView.BTN_REMOVE_ALL):
						this.removeAllNodes();
						dispatch.reset();
						break;
				}
				
				
				//update mesh and routing selectors
				ui.updateRoutingSelectors(nodes);
				ui.update();
			}
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
		/*
		 * TODO
		 * Lines 304-310 are always called twice (self adds pair, pair adds self) x2
		 * with self and pair being swapped between calls
		 * 
		 * While the LowpanNode data-type prevents redundant neighbors and guards against
		 * non-applicable removals, this is still unnecessary function calls.
		 * 
		 * Would be good to write a more elegant algorithm as opposed to the brute-force
		 * "compare all in N to all others in N" algorithm currently implemented
		 */
		
		//iterate for all nodes in manifest
		for (LowpanNode self : nodes)
		{
			//iterate for all nodes in manifest
			for (LowpanNode pair : nodes)
			{
				//compare all nodes to all other nodes that are not itself
				if (!self.equals(pair))
				{
					//check if radio-well intersection for easy radio
					//check for radio-well containing node for realistic radio
					double d = self.getLocation().distance(pair.getLocation());
					if ( (radioType && (self.getLocation().distance(pair.getLocation()) <= self.getRange()+pair.getRange()))
						|| (!radioType && (d <= self.getRange() && d <= pair.getRange())) )
					{
						self.addNeighbour(pair);
						pair.addNeighbour(self);
					}
					else
					{
						self.removeNeighbour(pair);
						pair.removeNeighbour(self);
					}
				}
			}
		}
	}
	
	
	//handle resizing of main window
	@Override
	public void componentResized(ComponentEvent arg0)
	{
		for (LowpanNode node : nodes)
		{
			node.validateLocation();
		}
	}
	
	
	@Override public void keyReleased(KeyEvent ke) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	@Override public void mousePressed(MouseEvent arg0) {}
	@Override public void mouseReleased(MouseEvent arg0) {}
	@Override public void keyTyped(KeyEvent arg0) {}
	@Override public void componentHidden(ComponentEvent arg0) {}
	@Override public void componentMoved(ComponentEvent arg0) {}
	@Override public void componentShown(ComponentEvent arg0) {}
	
	
	//main runtime
	public static void main(String[] args)
	{
		//parse arguments
		boolean fullscreen = false;
		for (String flag : args)
		{
			switch (flag)
			{
				//fullscreen flag
				case("-f"):
				case("--fullscreen"):
					fullscreen = true;
					break;
				
				//unknown flag
				default:
					System.out.println("Unknown flag \"" + flag + "\" found\nTerminating...\n");
					System.exit(0);
					break;
			}
		}
		
		//create new simulator
		LowpanSim sim = new LowpanSim(fullscreen);
	}
}