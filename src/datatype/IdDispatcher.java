/**
 * Class:				IdDispatcher.java
 * Project:				Lowpan Network Sim
 * Author:				Jason Van Kerkhoven
 * Date of Update:		22/01/2017
 * Version:				1.2.1
 * 
 * Purpose:				Gives the next valid ID address up to 2^32-1.
 * 						If you exceed the limit it will crash because.
 * 						Ripped and ported from old code.
 * 
 * Update Log:			v1.0.0
 * 							- null
 */
package datatype;


//import libraries
import java.util.LinkedList;




public class IdDispatcher
{
	//declaring local instance variables
	private LinkedList<Integer> retired;
	private int newId;
	private boolean full;
	
	
	//generic constructor
	public IdDispatcher()
	{
		retired = new LinkedList<>();
		newId = 0;
		full = false;
	}
	
	
	//get next available ID
	public int getNextID()
	{
		//there is 1 or more retired IDs available, use the lowest value among them
		if (retired.size() > 0)
		{
			return (int)retired.pop();
		}
		//no retired IDs available, create new ID
		else if (!full)
		{
			int ret = newId;
			newId++;
			if(newId == Integer.MAX_VALUE)
			{
				full = true;
			}
			return ret;
		}
		else
		{
			System.exit(0);
			return 0;
			//out of IDs just exit
		}
	}
	
	
	//exactly what it says on the tin
	public void reset()
	{
		retired.clear();
		full = false;
		newId = 0;
	}
	
	
	//retire an ID
	public void retireID(int toAdd)
	{
		if(retired.size() == 0)
		{
			retired.addFirst((Integer)toAdd);
		}
		else
		{
			//iterate through list to determine where to put it 
			for(int i=0; i<retired.size(); i++)
			{
				//new int should come before int in list
				if(toAdd < retired.get(i).intValue() )
				{
					retired.add(i, toAdd);
					return;
				}
			}
			//add to end
			retired.addLast((Integer)toAdd);
		}
	}
	
	
	@Override
	//return as a String
	public String toString()
	{
		String string = "newId: " + newId + ", full: " + full + ", retired: {";
		
		boolean first = true;
		for (int i : retired)
		{
			if(first) 
			{
				first=false;
			}
			else 
			{
				string += ", ";
			}
			
			string += i;
		}
		string += "}";
				
		return string;
	}
}