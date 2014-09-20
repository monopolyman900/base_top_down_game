package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import animation.Animation;
import animation.CollidableObject;
import utilities.ItemDescriptions;

public class Item extends CollidableObject implements Serializable{
	
	private static final long serialVersionUID = 006L;

	String ID;
	int x, y;
	boolean selected;
	boolean inventoryDisplayed;
	
	private boolean isAlwaysRelevant; 
	private boolean isAlive; 
	private boolean isSleeping; 
	private boolean isFlipped;
	private boolean isItem;
	private boolean isPlatform;
	private boolean isInvisible;
	private boolean isPenatrable;
	
	ItemDescriptions itemdesc = new ItemDescriptions();
	
	public Animation defaultAnim;
	
	public Item() { 
		this(0, 0, null);
	}
	
	public Item(String ID) { 
		this(0, 0, ID);
	}
	
	/**
	 * @effects Creates a new structure at the given pixelX, pixelY position
	 * 
	 * True: Collidable, Alive, Sleeping, Flipped.
	 * False: OnScreen, Item, Platform, Relevant.
	 */
	
	public Item(int pixelX, int pixelY, String ID){
		super(pixelX, pixelY, ID);
		this.ID = ID;

	}
	
	public Animation getDefaultAnim(){
		return defaultAnim;
	}
	
	public void setDefaultAnim(Animation anim){
		defaultAnim = anim;
	}
	
	public ArrayList<String> getDescription(String ID){
		ArrayList<String> desc = itemdesc.getItemDescription(ID);
		return desc;
	}
		
	public String getID(){
		return ID;
	}
	
	public void setID(String ID){
		this.ID = ID;
	}
	
	public void setX(int x){
		this.x = x;
		super.setX((float) x);
	}
	
	public void setY(int y){
		this.y = y;
		super.setY((float) y);
	}
	
	public void select(){
		this.selected = true;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	
	public boolean getSelected(){
		return selected;
	}
	
	public boolean inventoryDisplayed(){
		return inventoryDisplayed;
	}
	
	public void setInventoryDisplayed(boolean inventoryDisplayed){
		this.inventoryDisplayed = inventoryDisplayed;
	}
	
}
