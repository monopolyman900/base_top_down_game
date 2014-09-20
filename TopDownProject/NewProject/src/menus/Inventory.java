package menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import base.Item;
import base.Menu;
import base.Weapon;

//height: 312
//width:  402
//position: 45, 17
public class Inventory extends Menu implements Serializable{
	
	private static final long serialVersionUID = 004L;
	
	//items player has in inventory
	public ArrayList<Item> inventoryItems;
	
	int itemTitleX = 55;
	int itemTitleY;
	int itemTitleDiff = 15;
	
	int itemDescX = 265;
	int itemDescY = 40;
	
	public Weapon equippedWeapon;
	
	public String menuTitle;
	
	//find first item in inventory displayed
	int firstInventoryDisplayedIndex;
	int selectedInventoryItemIndex;
	
	public Inventory(){
		super.setMenu("menus/inventory.png");
		inventoryItems = new ArrayList<Item>();
	}
	
	public void equipWeapon(Weapon weapon){
		this.equippedWeapon = weapon;
	}
	
	public Weapon getEquippedWeapon(){
		return equippedWeapon;
	}
	
	public void draw(Graphics g, int x, int y) {
		//draw menu, menu title
		g.drawImage(menu, x, y, null);
		//find first inventory item displayed
		for(int i = 0; i < inventoryItems.size(); i++){
			if(inventoryItems.get(i).inventoryDisplayed()){
				firstInventoryDisplayedIndex = i;
				break;
			}
		}
		
		//load text for items in inventory
		//itemDisplayCount used to calculate Y position regardless of item count
		int itemDisplayedCount = 1;
		for(int i = (firstInventoryDisplayedIndex); i < inventoryItems.size(); i++){
			itemTitleY = 25 + (itemDisplayedCount*itemTitleDiff);
			itemDisplayedCount++;
			
			//draw only 20 item IDs in inventory at once
			if(inventoryItems.get(i).inventoryDisplayed() == true){
				if(inventoryItems.get(i).getSelected()){
					selectedInventoryItemIndex = i;
					g.setColor(Color.WHITE);
					ArrayList<String> desc = inventoryItems.get(selectedInventoryItemIndex).getDescription(inventoryItems.get(selectedInventoryItemIndex).getID());
					g.drawString(menuTitle, itemDescX, 195);
					if(desc != null && desc.size() > 0){
						for(int j = 0; j < desc.size(); j++){
							g.drawString(desc.get(j), itemDescX, (itemDescY+(itemTitleDiff*j)));
						}
					}
					ArrayList <String> instructions = getMenuInstructions(this);
					for(int j = 0; j < instructions.size(); j++){
						g.drawString(instructions.get(j), itemDescX, (210+(itemTitleDiff*j)));
					}
				}
				else{
					g.setColor(Color.GRAY);
				}
					if(inventoryItems.get(i) != this.getEquippedWeapon()){
						g.drawString(inventoryItems.get(i).getID(), itemTitleX, itemTitleY);
					}
					else{
						g.drawString((inventoryItems.get(i).getID()+" (EQUIPPED)"), itemTitleX, itemTitleY);
					}
			}
		}
		//draw menuTitle for when inventory is empty.
		if(inventoryItems.size() == 0){
			g.setColor(Color.WHITE);
			g.drawString(menuTitle, itemDescX, 195);
		}
	}
	
	public void moveUp(){
		for(int i = 0; i < inventoryItems.size(); i++){
			if (inventoryItems.get(i).getSelected() == true){
				if(i > 0){
					inventoryItems.get(i).setSelected(false);
					inventoryItems.get(i - 1).setSelected(true);
					if((inventoryItems.get(i -1).getSelected() == true) && (inventoryItems.get(i - 1).inventoryDisplayed() != true)){
						inventoryItems.get(i + 18).setInventoryDisplayed(false);
						inventoryItems.get(i - 1).setInventoryDisplayed(true);
					}
					break;
				}
			}
		}
	}
	
	public void moveDown(){
		for(int i = 0; i < inventoryItems.size(); i++){
			if (inventoryItems.get(i).getSelected() == true){
				if(i < inventoryItems.size() - 1){
					inventoryItems.get(i).setSelected(false);
					inventoryItems.get(i + 1).setSelected(true);
					if((inventoryItems.get(i + 1).getSelected() == true) && (inventoryItems.get(i + 1).inventoryDisplayed() != true)){
						inventoryItems.get(i - 18).setInventoryDisplayed(false);
						inventoryItems.get(i + 1).setInventoryDisplayed(true);
					}
					break;
				}
			}
		}
	}
	
	public void addItem(Item item){
		inventoryItems.add(item);
	}
	
	public void removeItem(Item item){
		for(int i = 0; i < inventoryItems.size(); i++){
			if(inventoryItems.get(i).getID() == item.getID()){
				inventoryItems.remove(i);
				break;
			}
		}
	}
	
	public void setMenuTitle(String title){
		this.menuTitle = title;
	}
	
	public ArrayList<Item> getInventoryItems(){
		return inventoryItems;
	}
	
	public Item getSelectedInventoryItem(){
		return inventoryItems.get(selectedInventoryItemIndex);
	}
	
	public int getSelectedItemIndex(){
		return selectedInventoryItemIndex;
	}
	
	public ArrayList <String> getMenuInstructions(Inventory i){
		ArrayList<String> instructions = new ArrayList<String>();
		//chest inventory first
		if(i instanceof ChestInventory){
			if(i.getInventoryItems().size() > 0){
				instructions.add("Press Enter to Take Selected");
				instructions.add("Item.");
			}
		}
		//else - this
		else{
			
		}
		return instructions;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		super.setMenu("menus/inventory.png");
	}
}
