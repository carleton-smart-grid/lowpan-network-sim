package datatype;

import java.util.HashSet;

import datatype.LowpanNode;

public class TreeNode<k>
{
	//declaring local instance variables
	private TreeNode<k> parent;
	private k self;
	private HashSet<TreeNode> children;
	
	
	//generic constructor for node
	public TreeNode(TreeNode parent, k self)
	{
		this.parent = parent;
		this.self = self;
		children = new HashSet<TreeNode>();
	}
	
	
	//generic getters
	public HashSet<TreeNode> getChildren()
	{
		return children;
	}
	public k getSelf()
	{
		return self;
	}
	
	
	//add child
	public TreeNode<k> addChild(k child)
	{
		TreeNode<k> newChild = new TreeNode<k>(this, child);
		children.add(newChild);
		return newChild;
	}
}
