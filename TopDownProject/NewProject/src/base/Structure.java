package base;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.sound.sampled.Line;

import player.Player;
import structures.House1;
import tile.GameTile;
import tile.TileMap;
import core.GameRenderer;
import animation.CollidableObject;
import animation.Sprite;

public class Structure extends CollidableObject{

	protected static int xCollideOffset = 2; 
	protected static int offMapOffset = 15;
	
	// Wake up values are constants based on the number of tiles on the screen
	// that are used to determine when mario comes within range of a structure.
	// Used exclusively within GameRender.java.
	public static int WAKE_UP_VALUE_DOWN_RIGHT = 24;
	public static int WAKE_UP_VALUE_UP_LEFT = -3;
	
	/* 
	 * structure Attributes:
	 * 
	 * Relevant:  A structure that is always relevant must be updated every frame. By default, no structure
	 * 			  is always relevant. 
	 * Alive:     A structure that is on the map is alive. All structures start alive and can be killed using
	 * 			  the kill() method.
	 * Sleeping:  A structure that is sleeping has yet to be seen by the player. All structures start out
	 * 			  sleeping, and can be woken up using wakeUp(). They cannot be put back to sleep.
	 * Flipped:   isFlipped is a flag used to determine when to change the animation of a structure to death.
	 * 			  For example, a goomba that is hopped on is 'flipped', then removed from the game.
	 * Item:      A structure that is an item represents an item the player can interact with.
	 * Platform:  A structure is a platform if it is a non-aligned moving object the player
	 * 			  and structures can interact with. 
	 * Invisible: When a structure is invisible, it isn't drawn.
	 */
	private boolean isAlwaysRelevant; 
	private boolean isAlive; 
	private boolean isSleeping; 
	private boolean isFlipped;
	private boolean isItem;
	private boolean isPlatform;
	private boolean isInvisible;
	
	private boolean isPenatrable;
	
	private String ID;
	
	public Structure() { 
		this(0, 0, null);
	}
	
	/**
	 * @effects Creates a new structure at the given pixelX, pixelY position
	 * 
	 * True: Collidable, Alive, Sleeping, Flipped.
	 * False: OnScreen, Item, Platform, Relevant.
	 */
	
	public Structure(int pixelX, int pixelY, String ID){
		super(pixelX, pixelY, ID);
		this.ID = ID;
		setIsCollidable(true);
		isAlive = true;
		isSleeping = true;
		isFlipped = false;
		setIsOnScreen(false);
		isItem = false;
		isPlatform = false;
		isAlwaysRelevant = false;
		isPenatrable = false;

	}
	
	/**
	 * @return true if this structure is a Platform, false otherwise.
	 */
	public boolean isPlatform() {
		return isPlatform;
	}
	
	/**
	 * @modifies the platform status of this structure.
	 */
	public void setIsPlatform(boolean isPlatform) {
		this.isPlatform = isPlatform;
	}
	
	/**
	 * @return true if this structure is an Item, false otherwise.
	 */
	public boolean isItem() {
		return isItem;
	}
	
	/**
	 * @modifies the item status of this structure.
	 */
	public void setIsItem(boolean isItem) {
		this.isItem = isItem;
	}
	
	/**
	 * @return true if this structure is flipped, false otherwise.
	 */
	public boolean isFlipped() {
		return isFlipped;
	}
	
	/**
	 * @modifies the flipped status of this structure.
	 */
	public void setIsFlipped(boolean isFlipped) {
		this.isFlipped = isFlipped;
	}
	
	/**
	 * @return true if this structure is sleeping, false otherwise.
	 */
	public boolean isSleeping() {
		return isSleeping;
	}
	
	/**
	 * @modifies the sleeping status of this structure to false.
	 */
	public void wakeUp() { 
		isSleeping = false;
	}
	
	/**
	 * @return true if this structure is alive, false otherwise.
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * @modifies the life state of this structure (alive or dead) to dead.
	 */
    public void kill() {
    	isAlive = false;
    }
    
	/**
	 * @return true if this structure is a Platform, false otherwise.
	 */
	public boolean isAlwaysRelevant() {
		return isAlwaysRelevant;
	}
	
	/**
	 * @modifies the platform status of this structure.
	 */
	public void setIsAlwaysRelevant(boolean isAlwaysRelevant) {
		this.isAlwaysRelevant = isAlwaysRelevant;
	}
	
	/**
	 * @return true if this structure is invisible, false otherwise.
	 */
	public boolean isInvisible() {
		return isInvisible;
	}
	
	/**
	 * @modifies the invisible status of this structure.
	 */
	public void setIsInvisible(boolean isInvisible) {
		this.isInvisible = isInvisible;
	}
	
	public String getID() {
		return ID;
	}
	
	public boolean setID() {
		return isInvisible;
	}
	
	public void setPenatrable(boolean penatrable){
		this.isPenatrable = penatrable;
	}
	
	public boolean inPenatrable(){
		return isPenatrable;
	}
	
	public void jumpedOn() { }
	public void flip() { }
	
	// for tile collisions
	public void xCollide(Point p) {
		if(dx > 0) {
			x = x - xCollideOffset;
		} else {
			x = x + xCollideOffset;
		}
		dx = -dx;
	}
	
	// for structure collisions
	public void structureXCollide() {
		if(dx > 0) {
			x = x - xCollideOffset;
		} else {
			x = x + xCollideOffset;
		}
		dx = -dx;
	}
	
	/**
	 * Calculates the type of collision in the X direction between a Tile 
	 * and a Sprite given the Sprite is currentely colliding with the tile. 
	 * This method relies on the general heuristic that if two 
	 * rectangular objects are colliding, then one object is not completely
	 * contained in the other. Because the colliding objects stick out, we
	 * know the direction of the collision. 
	 * 
	 * pre-condition: sprite is colliding with tile.
	 * @return Collision.WEST if sprite is colliding with the tile from the west or
	 * Collision.EAST if sprite is colliding with the tile from the east.
	 */
	public static Collision tileCollisionX(GameTile tile, Sprite s) {
		if(s.getX() > tile.getPixelX()) {
			return Collision.WEST;
		} else {
			return Collision.EAST;
		}
	}
	
	/**
	 * Calculates the type of collision in the Y direction between a Tile 
	 * and a Sprite given the Sprite is currentely colliding with the tile. 
	 * This method relies on the general heuristic that if two 
	 * rectangular objects are colliding, that one object is not completely
	 * contained in the other. Because the colliding objects stick out, we
	 * know the direction of the collision. 
	 * 
	 * pre-condition: sprite is colliding with tile.
	 * @return Collision.NORTH if sprite is colliding with the tile from the north or
	 * Collision.SOUTH if sprite is colliding with the tile from the south.
	 */
	public static Collision tileCollisionY(GameTile tile, Sprite s) {
		if(s.getY() < tile.getPixelY()) {
			return Collision.NORTH;
		} else {
			return Collision.SOUTH;
		}
	}
	
	public void buildStructures(TileMap map){
		//House1 house = new House1(200, 100, "house1");
		//house.createWalls();
		//map.structures().add(house);
	}
	
	public void updatestructure(TileMap map, int time){
		
		float dx = this.dx;
		float oldX = this.x;
		float newX = oldX + dx * time;
		
		float dy = this.dy;
		float oldY = this.y;
		float newY = oldY + dy * time; 
		
		if(!isFlipped) {
			Point xTile = GameRenderer.getTileCollision(map, this, x, y, newX, y);
			Point yTile = GameRenderer.getTileCollision(map, this, x, y, x, newY);
			
			this.update(time);
		
		}
	}
	
	public void playerCollision(float dx, float dy, Player player){

	}	
	
	// Determines what happens when two different structures collide.
	// Uncommenting the onSreen condition makes this more efficient, but more buggy
	public void structureCollision(Structure structure) {	
		/**
		 * Handle collisions for specific types of structures here
		 * Sample code below
		 * 
		 */
		/*
		
		if(!this.isItem && !structure.isItem && !this.isPlatform && !structure.isPlatform &&
				this != structure && this.isCollidable() && structure.isCollidable()) {
			
			boolean collision = isCollision(this, structure);
			if(collision) {	
				// Handeling RedShell collision cases....
				// ______________________________________
				// structure 1 is a RedShell, structure 2 is not.
				if(this instanceof RedShell && !(structure instanceof RedShell)) {
					if(((RedShell) this).isMoving()) {
						structure.flip();
						soundManager.playKick();
					}
				// structure 2 is a RedShell, structure 1 is not.
				} else if(!(this instanceof RedShell) && structure instanceof RedShell) {
					if(((RedShell) structure).isMoving()) {
						this.flip();
					}
				// both structure 1 and structure 2 are RedShells
				} else if(this instanceof RedShell && structure instanceof RedShell) {
					//RedShell 1 is moving, RedShell 2 is not.
					this.flip();
					structure.flip();
				// End of RedShell collision cases...
			    //____________________________________________
					
			    } else {
			    	this.structureXCollide();
			    	structure.structureXCollide();
			    }
				
				*/
	}
}
