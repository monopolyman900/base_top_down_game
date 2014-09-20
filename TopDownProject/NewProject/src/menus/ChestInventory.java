package menus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import base.Item;

public class ChestInventory extends Inventory implements Serializable{
	
	private static final long serialVersionUID = 003L;

	int chestNumber;
	
	public ChestInventory(int chestNumber){
		this.chestNumber = chestNumber;
		//super.setMenu("menus/inventory.png");
		setChestContents();
	}
	
	public void setChestContents(){
		switch (chestNumber){
		case 1:
			inventoryItems.add(new Item("TestItem"));
			inventoryItems.add(new Item("TestItem2"));
			inventoryItems.add(new Item("TestItem3"));
			break;
		case 2:
			inventoryItems.add(new Item("Item1"));
			inventoryItems.add(new Item("Item2"));
			inventoryItems.add(new Item("Item3"));
			break;
		}
	}
	
	public int getNumberOfItem(String itemID){
		int itemNum = 0;
		for(int i = 0; i < inventoryItems.size(); i++){
			if(inventoryItems.get(i) != null){
				if(inventoryItems.get(i).getID().equals(itemID)){
					itemNum++;
				}
			}
		}
		return itemNum;
	}
}
