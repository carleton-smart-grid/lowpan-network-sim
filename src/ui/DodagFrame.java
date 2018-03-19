/**
 * Jason Van Kerkhoven
 * 19-03-2018
 * 
 */
package ui;

//import libraries
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Rectangle;

//import packages
import datatype.LowpanNode;
import datatype.TreeNode;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Window.Type;
import java.text.SimpleDateFormat;
import java.util.Date;




public class DodagFrame extends JFrame 
{
	public DodagFrame(TreeNode<LowpanNode> parent, JFrame caller) 
	{
		//setup frame
		this.setBounds(new Rectangle(350,350));
		this.setLocationRelativeTo(caller);
		this.setAlwaysOnTop(true);
		this.setIconImage(new ImageIcon("icon.gif").getImage());
		this.setTitle("DODAG Tree @ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		//add displayable text
		JScrollPane scrollPane = new JScrollPane();
		JTextArea display = new JTextArea();
		display.setEditable(false);
		display.setBorder(null);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(display);
		
		//set text and visible
		display.setText(parent.toString());
		this.setVisible(true);
	}

}
