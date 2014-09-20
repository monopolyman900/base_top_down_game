package core;

import java.util.ArrayList;

import base.Item;
import items.Bucket;
import tile.TileMap;
import weapons.Axe;

/**
 * 
 * When adding items we have to manually add to the possible items
 * here as well as add to the newClonedItem functionality.
 *
 */
public class ItemManager {
	
	public ArrayList<Item> possibleItems;
	
	public ItemManager(){
		possibleItems = new ArrayList<Item>();
		possibleItems.add(new Bucket());
		possibleItems.add(new Axe());
	}
	
	public void initializeItems(TileMap map){
		map.items().add(new Bucket(3500, 1500, "bucket"));
		map.items().add(new Axe(3500, 1600, "axe"));
	}
	
	public Item newClonedItem(Item item){
		Item newItem = null;
		switch (item.getID()){
		case "bucket" : newItem = new Bucket();
		break;
		case "axe" : newItem = new Axe();
		break;
		}
		return newItem;
	}
}
