/**
 * Class:				TreeNode.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		12/02/2017
 * Version:				1.0.0
 * 
 * Purpose:				Basic node for tree structure
 * 
 * Update Log:			v1.0.1
 * 							- parameterized issues fixed
 * 						v1.0.0
 * 							- null
 */
package datatype;



//import library classes
import java.util.HashSet;




public class TreeNode<k>
{
	//declaring local instance variables
	private TreeNode<k> parent;
	private k self;
	private HashSet<TreeNode<k>> children;
	
	
	//generic constructor for node
	public TreeNode(TreeNode<k> parent, k self)
	{
		this.parent = parent;
		this.self = self;
		children = new HashSet<TreeNode<k>>();
	}
	
	
	//generic getters
	public HashSet<TreeNode<k>> getChildren()
	{
		return children;
	}
	public k getSelf()
	{
		return self;
	}
	public TreeNode<k> getParent()
	{
		return parent;
	}
	
	
	//add child
	public TreeNode<k> addChild(k child)
	{
		TreeNode<k> newChild = new TreeNode<k>(this, child);
		children.add(newChild);
		return newChild;
	}
	
	
	@Override
	//print as string
	public String toString()
	{
		return toStringHelper(this, "");
	}
	private String toStringHelper(TreeNode<k> curr, String offset)
	{
		String s = offset + curr.self.toString() + "\n";
		offset = offset + "    ";
		
		if (curr.children != null)
		{
			for (TreeNode<k> node : curr.children)
			{
				s += toStringHelper(node, offset);
			}
		}
		
		return s;
	}
}
