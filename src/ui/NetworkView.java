/**
 * Class:				NetworkView.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		12/02/2017
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
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.Dimension;

//link packages
import datatype.LowpanNode;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;




public class NetworkView extends JFrame implements ActionListener, SizeReporter
{	
	//declaring static class constants
	public static final String BTN_UPDATE = "btn/update";
	public static final String BTN_REMOVE = "btn/remove";
	public static final String BTN_REMOVE_ALL = "btn/removeall";
	public static final String BTN_NEW_NODE = "btn/newnode";
	public static final String RADIO_TYPE_A = "btn/radioA";
	public static final String RADIO_TYPE_B = "btn/radioB";
	public static final String PRESETS[] = {"1.   \"Louise Linear\"",
											"2.   \"Tina Tree\"",
											"3.   \"Stella Sparse\"",
											"4.   \"Clara Cluster\"",
											"5.   \"Martha Matrix\""};
	public static final String MENU_SHOW_DODAG = "menu/vis/dodag";
	private static final int DEFAULT_WINDOW_X = 1301;
	private static final int DEFAULT_WINDOW_Y = 721;
	private static final int AUX_PANEL_WIDTH = 275;
	private static final int RP_HEIGHT = 150;
	private static final Font INPUT_LABEL_FONT = new Font("Tahoma", Font.BOLD, 16);
	private static final Font INPUT_FIELD_FONT = new Font("Tahoma", Font.PLAIN, 12);
	
	//declaring local instance variables
	private LowpanNode activeNode;
	private NodeCanvas canvasPane;
	private JTextField nameField;
	private JTextField rangeField;
	private JTextField xField;
	private JTextField yField;
	private JCheckBox toggleDistances, toggleLabels;
	private JTextField labelID;
	private JTextField sourceLabel;
	private JTextField destLabel;
	private JComboBox<LowpanNode> sourceSelector;
	private JComboBox<LowpanNode> destinationSelector;
	private JComboBox<LowpanNode> rootNodeSelector;
	private JRadioButton radioMeshActiveNode, radioMeshAllNode;
	private JRadioButton radioWellsActiveNode, radioWellsAllNode;
	private JCheckBox rplRoutingToggle;
	private JCheckBox idealRoutingToggle;
	
	
	//generic constructor
	public NetworkView(String title,
			HashSet<LowpanNode> nodes,
			boolean fullScreen,
			MouseListener mouseListener,
			ActionListener actionListener,
			KeyListener keyListener,
			ComponentListener componentListener)
	{
		//set up main window frame
		super(title);
		this.setBounds(0, 0, DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y);
		this.setMinimumSize(new Dimension(0, DEFAULT_WINDOW_Y));
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(new ImageIcon("img/icon.gif").getImage());		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		//init non-gui components
		this.activeNode = null;
		
		//set up menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu presetsMenuBin = new JMenu("Presets");
		JMenu visualizationMenuBin = new JMenu("Visualization");
		menuBar.add(presetsMenuBin);
		menuBar.add(visualizationMenuBin);
		this.setJMenuBar(menuBar);
		
		//add presets to preset bin
		JMenuItem[] preset = new JMenuItem[PRESETS.length];
		for (int i=0; i<preset.length; i++)
		{
			preset[i] = new JMenuItem(PRESETS[i]);
			preset[i].setActionCommand(PRESETS[i]);
			preset[i].addActionListener(actionListener);
			presetsMenuBin.add(preset[i]);
		}
		
		//add to visualization bin
		JMenuItem showDodagTree = new JMenuItem("Show Current DODAG Tree");
		showDodagTree.setActionCommand(MENU_SHOW_DODAG);
		showDodagTree.addActionListener(this);
		visualizationMenuBin.add(showDodagTree);

		//add main canvas for network
		canvasPane = new NodeCanvas(nodes);
		canvasPane.setBackground(Color.WHITE);
		canvasPane.setBorder(BorderFactory.createLineBorder(Color.black));
		canvasPane.setPreferredSize(new Dimension(DEFAULT_WINDOW_X, DEFAULT_WINDOW_Y));
		canvasPane.addMouseListener(mouseListener);
		canvasPane.addKeyListener(keyListener);
		canvasPane.addComponentListener(componentListener);
		contentPane.add(canvasPane, BorderLayout.CENTER);
		
		//add aux pane for node info/log
		JPanel auxPane = new JPanel();
		//auxPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
		inputPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
		auxSubPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		nodePanel.add(auxSubPanel);
		
		//add button to add new nodes
		JButton btnNewNode = new JButton("New Node");
		btnNewNode.setActionCommand(BTN_NEW_NODE);;
		btnNewNode.setBounds(10, 11, 120, 23);
		btnNewNode.addActionListener(actionListener);
		auxSubPanel.add(btnNewNode);
		
		//add button to clear nodes
		JButton btnClearNodes = new JButton("Clear All Nodes");
		btnClearNodes.setActionCommand(BTN_REMOVE_ALL);
		btnClearNodes.setBounds(147, 11, 120, 23);
		btnClearNodes.addActionListener(actionListener);
		auxSubPanel.add(btnClearNodes);
		
		//add signal well options
		ButtonGroup signalWells = new ButtonGroup();
		JTextField labelSignalWells = new JTextField();
		labelSignalWells.setText("Draw Signal Wells:");
		labelSignalWells.setBounds(10, 45, 120, 23);
		labelSignalWells.setColumns(10);
		labelSignalWells.setEditable(false);
		labelSignalWells.setBorder(null);
		auxSubPanel.add(labelSignalWells);
		
		radioWellsActiveNode = new JRadioButton("Active Node Only");
		radioWellsActiveNode.setSelected(true);
		radioWellsActiveNode.setBounds(132, 45, 135, 23);
		radioWellsActiveNode.addActionListener(this);
		signalWells.add(radioWellsActiveNode);
		auxSubPanel.add(radioWellsActiveNode);
		
		radioWellsAllNode = new JRadioButton("All Nodes");
		radioWellsAllNode.setBounds(132, 71, 135, 23);
		radioWellsAllNode.addActionListener(this);
		signalWells.add(radioWellsAllNode);
		auxSubPanel.add(radioWellsAllNode);
		
		//add mesh edge options
		ButtonGroup meshEdges = new ButtonGroup();
		JTextField labelMeshEdges = new JTextField();
		labelMeshEdges.setText("Draw Mesh Edges:");
		labelMeshEdges.setBounds(10, 104, 120, 23);
		labelMeshEdges.setColumns(10);
		labelMeshEdges.setEditable(false);
		labelMeshEdges.setBorder(null);
		auxSubPanel.add(labelMeshEdges);
		
		radioMeshActiveNode = new JRadioButton("Active Node Only");
		radioMeshActiveNode.setBounds(132, 104, 135, 23);
		radioMeshActiveNode.addActionListener(this);
		meshEdges.add(radioMeshActiveNode);
		auxSubPanel.add(radioMeshActiveNode);
		
		radioMeshAllNode = new JRadioButton("All Nodes");
		radioMeshAllNode.setBounds(132, 130, 135, 23);
		radioMeshAllNode.addActionListener(this);
		radioMeshAllNode.setSelected(true);
		meshEdges.add(radioMeshAllNode);
		auxSubPanel.add(radioMeshAllNode);
		
		//add radio types
		ButtonGroup radioButtons = new ButtonGroup();
		JTextField labelRadioType = new JTextField();
		labelRadioType.setText("Radio Type:");
		labelRadioType.setEditable(false);
		labelRadioType.setColumns(10);
		labelRadioType.setBorder(null);
		labelRadioType.setBounds(10, 156, 120, 23);
		auxSubPanel.add(labelRadioType);
		
		JRadioButton radioRadioB = new JRadioButton("\"Realistic\" Radio B");
		radioRadioB.setBounds(132, 182, 135, 23);
		radioRadioB.setActionCommand(RADIO_TYPE_B);
		radioRadioB.addActionListener(actionListener);
		radioButtons.add(radioRadioB);
		auxSubPanel.add(radioRadioB);
		
		JRadioButton radioRadioA = new JRadioButton("\"EasySim\" Radio A");
		radioRadioA.setBounds(132, 156, 135, 23);
		radioRadioA.setActionCommand(RADIO_TYPE_A);
		radioRadioA.addActionListener(actionListener);
		radioRadioA.setSelected(true);
		radioButtons.add(radioRadioA);
		auxSubPanel.add(radioRadioA);
		
		//add checkboxes for mesh display settings
		toggleDistances = new JCheckBox("Edge Labels");
		toggleDistances.setSelected(false);
		toggleDistances.setBounds(116, 223, 109, 23);
		toggleDistances.addActionListener(this);
		auxSubPanel.add(toggleDistances);
		
		toggleLabels = new JCheckBox("Node Labels");
		toggleLabels.setSelected(true);
		toggleLabels.setBounds(10, 223, 109, 23);
		toggleLabels.addActionListener(this);
		auxSubPanel.add(toggleLabels);

		//add text log to aux panel
		JPanel routingPanel = new JPanel();
		routingPanel.setLayout(null);
		routingPanel.setPreferredSize(new Dimension(0, RP_HEIGHT));
		routingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		auxPane.add(routingPanel, BorderLayout.SOUTH);
		
		//add labels to routing panel
		sourceLabel = new JTextField();
		sourceLabel.setText("Source:");
		sourceLabel.setBorder(null);
		sourceLabel.setEditable(false);
		sourceLabel.setFont(INPUT_LABEL_FONT);
		sourceLabel.setBounds(10, 11, 114, 20);
		routingPanel.add(sourceLabel);
		sourceLabel.setColumns(10);
		
		destLabel = new JTextField();
		destLabel.setText("Destination:");
		destLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		destLabel.setEditable(false);
		destLabel.setColumns(10);
		destLabel.setBorder(null);
		destLabel.setBounds(10, 42, 114, 20);
		routingPanel.add(destLabel);
		
		//add selectors for routing source/destination
		sourceSelector = new JComboBox<LowpanNode>();
		sourceSelector.setEnabled(false);
		sourceSelector.addActionListener(this);
		sourceSelector.setBounds(151, 12, 114, 22);
		routingPanel.add(sourceSelector);
		
		destinationSelector = new JComboBox<LowpanNode>();
		destinationSelector.setEnabled(false);
		destinationSelector.addActionListener(this);
		destinationSelector.setBounds(151, 43, 114, 22);
		routingPanel.add(destinationSelector);
		
		//add toggles for routing modes
		rplRoutingToggle = new JCheckBox("RPL Routing");
		rplRoutingToggle.setSelected(false);
		rplRoutingToggle.setBounds(10, 90, 100, 23);
		rplRoutingToggle.addActionListener(this);
		routingPanel.add(rplRoutingToggle);
		
		idealRoutingToggle = new JCheckBox("Ideal Routing");
		idealRoutingToggle.setSelected(false);
		idealRoutingToggle.setBounds(10, 120, 100, 23);
		idealRoutingToggle.addActionListener(this);
		routingPanel.add(idealRoutingToggle);
		
		//add label for DODAG select 
		JTextField dodagNodeLabel = new JTextField();
		dodagNodeLabel.setText("DODAG:");
		dodagNodeLabel.setEditable(false);
		dodagNodeLabel.setColumns(10);
		dodagNodeLabel.setBorder(null);
		dodagNodeLabel.setBounds(116, 91, 60, 20);
		routingPanel.add(dodagNodeLabel);
		
		//add DODAG select for RPL routing
		rootNodeSelector = new JComboBox<LowpanNode>();
		rootNodeSelector.setEnabled(false);
		rootNodeSelector.addActionListener(this);
		rootNodeSelector.setBounds(186, 90, 79, 22);
		routingPanel.add(rootNodeSelector);
		
		//set visible
		if (fullScreen)
		{
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			this.setUndecorated(true);
		}
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
		canvasPane.setActiveNode(node);
		updateNodeDisplay();
		this.update();
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
		}
		else
		{
			//set all blank
			labelID.setText("");
			nameField.setText("");
			rangeField.setText("");
			xField.setText("");
			yField.setText("");
		}
	}
	
	
	//update routing selectors
	public void updateRoutingSelectors(HashSet<LowpanNode> nodes)
	{
		//remove old nodes
		sourceSelector.removeAllItems();
		destinationSelector.removeAllItems();
		rootNodeSelector.removeAllItems();
		
		//add new items
		for (LowpanNode node : nodes)
		{
			sourceSelector.addItem(node);
			destinationSelector.addItem(node);
			rootNodeSelector.addItem(node);
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
	
	
	@Override
	//generic getters for NodeCanvas dimensions
	public Dimension getCurrentSize() 
	{
		return canvasPane.getSize();
	}
	
	
	@Override
	//generic getter for NodeCanvas X size
	public int getCurrentX() 
	{
		return canvasPane.getWidth();
	}
	
	
	@Override
	//generic getter for NodeCanvas Y size
	public int getCurrentY() 
	{
		return canvasPane.getHeight();
	}
	
	
	//redraw node canvas
	public void update()
	{
		canvasPane.repaint();
		updateNodeDisplay();
	}
	
	
	//get the current size of the NodeCanvas object
	public Dimension getCanvasSize() 
	{
		return canvasPane.getSize();
	}


	@Override
	//toggle mesh settings
	public void actionPerformed(ActionEvent arg) 
	{
		String cmd = arg.getActionCommand();
		
		//display DODAG in util window
		if (cmd.equals(MENU_SHOW_DODAG))
		{
			if (rplRoutingToggle.isSelected())
			{
				LowpanNode root = (LowpanNode)rootNodeSelector.getSelectedItem();
				
				if (root != null)
				{
					DodagFrame dodag = new DodagFrame(root.treeify(), this);
				}
				else
				{
					JOptionPane.showMessageDialog(this,
						    "Valid node must be selected as DODAG root.",
						    "DODAG Tree Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, 
						"RPL Routing must be enabled to construct DODAG tree.", 
						"DODAG Tree Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//button press or otherwise
		else
		{
			//set enable for edge distances and rpl root node selector
			toggleDistances.setEnabled(radioMeshActiveNode.isSelected() || radioMeshAllNode.isSelected());	
			rootNodeSelector.setEnabled(rplRoutingToggle.isSelected());
			
			//set enable for routing selectors
			boolean route = (idealRoutingToggle.isSelected() || rplRoutingToggle.isSelected());
			sourceSelector.setEnabled(route);
			destinationSelector.setEnabled(route);
			
			//update routing information		TODO these casts should be unnecessary
			if (route)
			{
				canvasPane.setRoutingNodes( (LowpanNode)sourceSelector.getSelectedItem(), 
											(LowpanNode)destinationSelector.getSelectedItem(),
											(LowpanNode)rootNodeSelector.getSelectedItem());
			}
			
			//set flags in node canvas
			canvasPane.setSignalWells(radioWellsActiveNode.isSelected(), radioWellsAllNode.isSelected());
			canvasPane.setMeshLines(radioMeshActiveNode.isSelected(), radioMeshAllNode.isSelected());
			canvasPane.setDistances(toggleDistances.isSelected());
			canvasPane.setNodeIds(toggleLabels.isSelected());
			canvasPane.setIdealRouting(idealRoutingToggle.isSelected());
			canvasPane.setRplRouting(rplRoutingToggle.isSelected());
			this.update();
		}
	}
}
