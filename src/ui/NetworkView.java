/**
 * Class:				NetworkView.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		25/11/2017
 * Version:				1.0.0
 * 
 * Purpose:				Graphical user interface for system.
 * 						Allows examination of specific node detials and editing of them.
 * 						Most changes are done through hot keys (arrows, +, -).
 * 
 * Update Log:			v1.0.0
 * 							- null
 */
package ui;

//import libraries
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Dimension;

//import packages
import ctrl.LowpanSim;
import datatype.LowpanNode;




public class NetworkView extends JFrame implements ActionListener 
{	
	//declaring static class constants
	public static final String BTN_UPDATE = "btn/update";
	public static final String BTN_REMOVE = "btn/remove";
	public static final String BTN_REMOVE_ALL = "btn/removeall";
	public static final String BTN_NEW_NODE = "btn/newnode";
	
	private static final int DEFAULT_WINDOW_X = 1301;
	private static final int DEFAULT_WINDOW_Y = 698;
	private static final int AUX_PANEL_WIDTH = 275;
	private static final int RP_HEIGHT = 150;
	private static final Font INPUT_LABEL_FONT = new Font("Tahoma", Font.BOLD, 16);
	private static final Font INPUT_FIELD_FONT = new Font("Tahoma", Font.PLAIN, 12);
	private static final Font CONNECTION_DISPLAY_FONT = new Font("Tahoma", Font.BOLD, 14);
	//private static final Font CONSOLE_FONT = new Font("Monospaced", Font.PLAIN, 13);
	
	//declaring local instance variables
	private JTextArea log;
	private NodeCanvas canvasPane;
	private JTextArea nodeInfoText;
	private JTextField nameField;
	private JTextField rangeField;
	private JTextField xField;
	private JTextField yField;
	private JCheckBox toggleMesh, toggleSignalWells, toggleLabels, toggleDistance;
	
	//declaring local instance variables
	LowpanNode activeNode;
	private JTextField labelID;
	
	//generic constructor
	public NetworkView(String title, HashSet<LowpanNode> nodes, MouseListener mouseListener, ActionListener actionListener, KeyListener keyListener)
	{
		//set up main window frame
		super(title);
		this.setBounds(0, 0, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		//init non-gui components
		activeNode = null;

		//add main canvas for network
		canvasPane = new NodeCanvas(nodes);
		canvasPane.setBackground(Color.WHITE);
		canvasPane.setBorder(BorderFactory.createLineBorder(Color.black));
		canvasPane.setPreferredSize(new Dimension(LowpanSim.MAX_X, LowpanSim.MAX_Y));
		canvasPane.addMouseListener(mouseListener);
		canvasPane.addKeyListener(keyListener);
		contentPane.add(canvasPane, BorderLayout.CENTER);
		
		//add aux pane for node info/log
		JPanel auxPane = new JPanel();
		auxPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		auxPane.setLayout(new BorderLayout(0, 0));
		auxPane.setPreferredSize(new Dimension(AUX_PANEL_WIDTH,0));
		contentPane.add(auxPane, BorderLayout.EAST);
		
		//add panel for node info/actions to aux
		JPanel nodePanel = new JPanel();
		nodePanel.setBorder(null);      //;
		auxPane.add(nodePanel, BorderLayout.CENTER);
		nodePanel.setLayout(new GridLayout(2, 1, 10, 0));
		
		//setup panel for user input
		JPanel inputPanel = new JPanel();
		nodePanel.add(inputPanel);
		inputPanel.setLayout(null);
		
		//add buttons to input panel
		JButton btnUpdate = new JButton("Update");
		JButton btnRemove = new JButton("Remove");
		btnUpdate.setBounds(10, 219, 120, 23);
		btnUpdate.setActionCommand(BTN_UPDATE);
		btnUpdate.addActionListener(actionListener);
		btnRemove.setBounds(143, 219, 120, 23);
		btnRemove.setActionCommand(BTN_REMOVE);
		btnRemove.addActionListener(actionListener);
		inputPanel.add(btnUpdate);
		inputPanel.add(btnRemove);
		
		//add labels for text inputs
		JTextField labelName = new JTextField();
		labelName.setFont(INPUT_LABEL_FONT);
		labelName.setText("Name:");
		labelName.setEditable(false);
		labelName.setBounds(10, 48, 86, 26);
		labelName.setColumns(10);
		labelName.setBorder(null);
		inputPanel.add(labelName);
		
		JTextField labelRange = new JTextField();
		labelRange.setText("Range:");
		labelRange.setFont(INPUT_LABEL_FONT);
		labelRange.setEditable(false);
		labelRange.setColumns(10);
		labelRange.setBounds(10, 85, 86, 26);
		labelRange.setBorder(null);
		inputPanel.add(labelRange);
		
		JTextField labelX = new JTextField();
		labelX.setText("X:");
		labelX.setFont(INPUT_LABEL_FONT);
		labelX.setEditable(false);
		labelX.setColumns(10);
		labelX.setBounds(10, 122, 86, 26);
		labelX.setBorder(null);
		inputPanel.add(labelX);
		
		JTextField labelY = new JTextField();
		labelY.setText("Y:");
		labelY.setFont(INPUT_LABEL_FONT);
		labelY.setEditable(false);
		labelY.setColumns(10);
		labelY.setBounds(10, 159, 86, 26);
		labelY.setBorder(null);
		inputPanel.add(labelY);
		
		nameField = new JTextField();
		nameField.setFont(INPUT_FIELD_FONT);
		nameField.setColumns(10);
		nameField.setBounds(106, 48, 157, 26);
		nameField.setActionCommand(BTN_UPDATE);
		nameField.addActionListener(actionListener);
		inputPanel.add(nameField);
		
		rangeField = new JTextField();
		rangeField.setFont(INPUT_FIELD_FONT);
		rangeField.setColumns(10);
		rangeField.setBounds(106, 85, 157, 26);
		rangeField.setActionCommand(BTN_UPDATE);
		rangeField.addActionListener(actionListener);
		inputPanel.add(rangeField);
		
		xField = new JTextField();
		xField.setFont(INPUT_FIELD_FONT);
		xField.setColumns(10);
		xField.setBounds(106, 122, 157, 26);
		xField.setActionCommand(BTN_UPDATE);
		xField.addActionListener(actionListener);
		inputPanel.add(xField);
		
		yField = new JTextField();
		yField.setFont(INPUT_FIELD_FONT);
		yField.setColumns(10);
		yField.setBounds(106, 159, 157, 26);
		yField.setActionCommand(BTN_UPDATE);
		yField.addActionListener(actionListener);
		inputPanel.add(yField);
		
		labelID = new JTextField();
		labelID.setHorizontalAlignment(SwingConstants.CENTER);
		labelID.setFont(INPUT_LABEL_FONT);
		labelID.setEditable(false);
		labelID.setColumns(10);
		labelID.setBorder(null);
		labelID.setBounds(10, 11, 253, 26);
		inputPanel.add(labelID);
		
		//add panel for display node connections and misc options
		JPanel auxSubPanel = new JPanel();
		auxSubPanel.setLayout(null);
		auxSubPanel.setBorder(null);
		nodePanel.add(auxSubPanel);
		
		//add button to add new nodes
		JButton btnNewNode = new JButton("New Node");
		btnNewNode.setActionCommand(BTN_NEW_NODE);;
		btnNewNode.setBounds(10, 167, 120, 23);
		btnNewNode.addActionListener(actionListener);
		auxSubPanel.add(btnNewNode);
		
		//add button to clear nodes
		JButton btnClearNodes = new JButton("Clear All Nodes");
		btnClearNodes.setActionCommand(BTN_REMOVE_ALL);
		btnClearNodes.setBounds(143, 167, 120, 23);
		btnClearNodes.addActionListener(actionListener);
		auxSubPanel.add(btnClearNodes);
		
		//add checkboxes for mesh display settings
		toggleMesh = new JCheckBox("Mesh Edges");
		toggleMesh.setSelected(true);
		toggleMesh.setBounds(147, 223, 120, 23);
		toggleMesh.addActionListener(this);
		auxSubPanel.add(toggleMesh);
		
		toggleSignalWells = new JCheckBox("Signal Wells");
		toggleSignalWells.setSelected(false);
		toggleSignalWells.setBounds(10, 223, 120, 23);
		toggleSignalWells.addActionListener(this);
		auxSubPanel.add(toggleSignalWells);
		
		toggleLabels = new JCheckBox("Node Labels");
		toggleLabels.setSelected(true);
		toggleLabels.setBounds(10, 197, 120, 23);
		toggleLabels.addActionListener(this);
		auxSubPanel.add(toggleLabels);
		
		toggleDistance = new JCheckBox("Edge Labels");
		toggleDistance.setSelected(false);
		toggleDistance.setEnabled(false);				//TODO remove later when done
		toggleDistance.setBounds(147, 197, 120, 23);
		toggleDistance.addActionListener(this);
		auxSubPanel.add(toggleDistance);
		
		//add text for connection print in scroll
		JScrollPane connectionScrollPane = new JScrollPane();
		connectionScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		connectionScrollPane.setBounds(10, 11, 253, 145);
		connectionScrollPane.setBorder(null);
		nodeInfoText = new JTextArea();
		nodeInfoText.setBounds(10, 153, 253, 3);
		nodeInfoText.setFont(CONNECTION_DISPLAY_FONT);
		nodeInfoText.setEditable(false);
		nodeInfoText.setWrapStyleWord(true);
		nodeInfoText.setBackground(nodePanel.getBackground());
		connectionScrollPane.setViewportView(nodeInfoText);
		auxSubPanel.add(connectionScrollPane);

		//add text log to aux panel
		JPanel routingPanel = new JPanel();
		routingPanel.setPreferredSize(new Dimension(0, RP_HEIGHT));
		auxPane.add(routingPanel, BorderLayout.SOUTH);
		
		//set visible
		this.setVisible(true);
	}
	
	
	//hacky solution to use key listeners
	public void enabledKeyInput()
	{
		canvasPane.setFocusable(true);
		canvasPane.requestFocusInWindow();
	}
	
	
	//update node view
	public void setActiveNode(LowpanNode node)
	{
		//update active node
		activeNode = node;
		updateNodeDisplay();
	}
	
	
	//update node display
	private void updateNodeDisplay()
	{
		if (activeNode != null)
		{
			//set node info
			labelID.setText("Node Number "+activeNode.getId());
			nameField.setText(activeNode.getName());
			rangeField.setText(""+activeNode.getRange());
			xField.setText(""+activeNode.getLocation().x);
			yField.setText(""+activeNode.getLocation().y);
			
			//set connections
			String s = "";
			for (LowpanNode node : activeNode.getNeighbours())
			{
				s += "n" + activeNode.getId() + " ---> n" + node.getId() + " \"" + node.getName() + "\"\n";
			}
			nodeInfoText.setText(s);
		}
		else
		{
			//set all blank
			labelID.setText("");
			nameField.setText("");
			rangeField.setText("");
			xField.setText("");
			yField.setText("");
			nodeInfoText.setText("");
		}
	}
	
	
	//generic input field getters
	public String getInputName()
	{
		return nameField.getText();
	}
	public Integer getInputRange()
	{
		try
		{
			return Integer.parseInt(rangeField.getText());
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	public Integer getInputX()
	{
		try
		{
			return Integer.parseInt(xField.getText());
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	public Integer getInputY()
	{
		try
		{
			return Integer.parseInt(yField.getText());
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	
	//get active node
	public LowpanNode getActiveNode()
	{
		return activeNode;
	}
	
	
	//redraw node canvas
	public void update()
	{
		canvasPane.repaint();
		updateNodeDisplay();
	}


	@Override
	//toggle mesh settings
	public void actionPerformed(ActionEvent arg0) 
	{
		canvasPane.setMeshLines(toggleMesh.isSelected());
		canvasPane.setSignalWells(toggleSignalWells.isSelected());
		canvasPane.setNodeIds(toggleLabels.isSelected());
		canvasPane.setDistances(toggleDistance.isSelected());
		this.update();
	}
}
