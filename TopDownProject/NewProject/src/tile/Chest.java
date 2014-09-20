package tile;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import menus.ChestInventory;

public class Chest extends ActionTile implements Serializable{
	
	private static final long serialVersionUID = 005L;

	//number to keep track of what chests have what in them.
	int chestNumber;
	public ChestInventory chestInventory;
	
	public Chest(int pixelX, int pixelY, BufferedImage img, boolean isCollidable, String ID, int cn) {
		super(pixelX, pixelY, img, isCollidable, ID);
		this.chestNumber = cn;
		chestInventory = new ChestInventory(chestNumber);
		chestInventory.setMenuTitle("Container Contents:");
	}
	
	public Chest(int pixelX, int pixelY, BufferedImage img, String ID, int cn) {
		this(pixelX, pixelY, img, true, ID, cn);
	}

	public void doAction(){
		if(!chestInventory.isDisplayed()){
			chestInventory.displayed = true;
			if(chestInventory.inventoryItems.size() > 0){
				chestInventory.inventoryItems.get(0).setSelected(true);
			}
			for(int i = 0; i < chestInventory.inventoryItems.size(); i++){
    			if(i < 19){
    				chestInventory.inventoryItems.get(i).setInventoryDisplayed(true);
    			}
    		}
			chestInventory.displayMenu();
		}
		else{
			chestInventory.hideMenu();
    		//set none to selected
    		for(int i = 0; i < chestInventory.inventoryItems.size(); i++){
    			/*
    			 * This stuff is for reinitializing purposes
    			 */
    			//set all items to not be inventory displayed
    			//set all inventory items to not selected -- first one is set to selected outside the loop
    			chestInventory.inventoryItems.get(i).setInventoryDisplayed(false);
    			chestInventory.inventoryItems.get(i).setSelected(false);
    		}
    		//set only first item in inventory to displayed
    		if(chestInventory.inventoryItems.size() > 0){
    			chestInventory.inventoryItems.get(0).setInventoryDisplayed(true);
    		}
    	}
	}
	
	public ChestInventory getInventory(){
		return chestInventory;
	}
}
