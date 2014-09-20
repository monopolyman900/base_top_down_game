package utilities;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemDescriptions implements Serializable{
	
	private static final long serialVersionUID = 013L;

	String desc;
	
	public ItemDescriptions(){
		
	}
	
	public ArrayList<String> getItemDescription(String ID){
		ArrayList<String> desc = new ArrayList<String>();
		switch (ID){
			case "Item1": 
			desc.add("item one description");
		break;
		case "bucket": 
			desc.add("This is a bucket.");
			desc.add("It can hold water and stuff.");
		break;
		case "axe": 
			desc.add("This is an axe.");
			desc.add("It can be used as a weapon.");
		break;
		}
		if(desc.size() == 0){
			desc.add("No description provided");
		}
		return desc;
	}
}
