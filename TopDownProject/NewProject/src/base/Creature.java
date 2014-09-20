package base;

import java.awt.Point;

import player.Player;
import core.GameRenderer;
import creatures.TumbleWeed;
import creatures.Zombie;
import base.Creature;
import tile.TileMap;
import tile.GameTile;
import animation.Sprite;
import animation.CollidableObject;

public class Creature extends CollidableObject{

	protected static int xCollideOffset = 2; 
	protected static int offMapOffset = 15;
	
	// Wake up values are constants based on the number of tiles on the screen
	// that are used to determine when the player comes within range of a creature.
	// Used exclusively within GameRender.java.
	public static int WAKE_UP_VALUE_DOWN_RIGHT = 24;
	public static int WAKE_UP_VALUE_UP_LEFT = -3;
	
	/* 
	 * Creature Attributes:
	 * 
	 * Relevant:  A creature that is always relevant must be updated every frame. By default, no creature
	 * 			  is always relevant. 
	 * Alive:     A creature that is on the map is alive. All creatures start alive and can be killed using
	 * 			  the kill() method.
	 * Sleeping:  A creature that is sleeping has yet to be seen by the player. All creatures start out
	 * 			  sleeping, and can be woken up using wakeUp(). They cannot be put back to sleep.
	 * Flipped:   isFlipped is a flag used to determine when to change the animation of a creature to death.
	 * 			  For example, a goomba that is hopped on is 'flipped', then removed from the game.
	 * Item:      A creature that is an item represents an item the player can interact with.
	 * Platform:  A creature is a platform if it is a non-aligned moving object the player
	 * 			  and creatures can interact with. 
	 * Invisible: When a creature is invisible, it isn't drawn.
	 */
	private boolean isAlwaysRelevant; 
	private boolean isAlive; 
	private boolean isSleeping; 
	private boolean isFlipped;
	private boolean isItem;
	private boolean isPlatform;
	private boolean isInvisible;
	
	public double damageLevel;
	
	public String ID;
	public boolean selected;
	
	public Creature() { 
		this(0, 0, null);
	}
	
	/**
	 * @effects Creates a new Creature at the given pixelX, pixelY position
	 * 
	 * True: Collidable, Alive, Sleeping, Flipped.
	 * False: OnScreen, Item, Platform, Relevant.
	 */
	
	public Creature(int pixelX, int pixelY, String ID){
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
	
	/**
	 * @return true if this creature is a Platform, false otherwise.
	 */
	public boolean isPlatform() {
		return isPlatform;
	}
	
	/**
	 * @modifies the platform status of this Creature.
	 */
	public void setIsPlatform(boolean isPlatform) {
		this.isPlatform = isPlatform;
	}
	
	/**
	 * @return true if this creature is an Item, false otherwise.
	 */
	public boolean isItem() {
		return isItem;
	}
	
	/**
	 * @modifies the item status of this Creature.
	 */
	public void setIsItem(boolean isItem) {
		this.isItem = isItem;
	}
	
	/**
	 * @return true if this creature is flipped, false otherwise.
	 */
	public boolean isFlipped() {
		return isFlipped;
	}
	
	/**
	 * @modifies the flipped status of this Creature.
	 */
	public void setIsFlipped(boolean isFlipped) {
		this.isFlipped = isFlipped;
	}
	
	/**
	 * @return true if this creature is sleeping, false otherwise.
	 */
	public boolean isSleeping() {
		return isSleeping;
	}
	
	/**
	 * @modifies the sleeping status of this creature to false.
	 */
	public void wakeUp() { 
		isSleeping = false;
	}
	
	/**
	 * @return true if this creature is alive, false otherwise.
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * @modifies the life state of this creature (alive or dead) to dead.
	 */
    public void kill() {
    	isAlive = false;
    }
    
	/**
	 * @return true if this creature is a Platform, false otherwise.
	 */
	public boolean isAlwaysRelevant() {
		return isAlwaysRelevant;
	}
	
	/**
	 * @modifies the platform status of this Creature.
	 */
	public void setIsAlwaysRelevant(boolean isAlwaysRelevant) {
		this.isAlwaysRelevant = isAlwaysRelevant;
	}
	
	/**
	 * @return true if this creature is invisible, false otherwise.
	 */
	public boolean isInvisible() {
		return isInvisible;
	}
	
	/**
	 * @modifies the invisible status of this Creature.
	 */
	public void setIsInvisible(boolean isInvisible) {
		this.isInvisible = isInvisible;
	}
	
	public String getID() {
		return ID;
	}
	
	public void attacked(Creature c, String direction, double damageLevel, Player p){
		if(c instanceof Zombie){
			Zombie z = ((Zombie) c);
			z.attacked(direction, damageLevel, p);
		}
	}
	
	public boolean setID() {
		return isInvisible;
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
	
	// for creature collisions
	public void creatureXCollide() {
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
	
	public void updateCreature(TileMap map, int time){
		
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
		if(this instanceof TumbleWeed){
			if(dx > 0 && (this.x >= ((player.getX()+player.getWidth()/2)))){
				this.dx = ((float)(dx*1.5));
			}
			else if(dx < 0){
				this.dx = ((float)(dx*3));
			}
			else if (dx == 0 && dy == 0 && (this.x < ((player.getX()+player.getWidth()/2)))){
				this.dx = 0;
			}
			else if(dy > 0){
				this.dy = ((float)(dy*1.5));
			}
			else if(dy < 0){
				this.dy = ((float)(dy*1.5));
			}
			else if (dy == 0 && (this.y < ((player.getY()+player.getWidth()/2)))){
				this.dy = 0;
			}
		}
		if(this instanceof Zombie){
			
			float leftSide;
			float rightSide;
			float upperSide;
			float lowerSide;
			
			leftSide = this.x;
			rightSide = this.x + this.getWidth();
			upperSide = this.y;
			lowerSide = this.y - this.getHeight();
			
			//left collision
			if(leftSide >= player.getX()){
				if(player.dx > 0){
					player.dx -= .5;
				}
				player.x -= 10;
			}
			//right collision
			if(rightSide <= player.getX() + player.getWidth()){
				if(player.dx < 0){
					player.dx += .5;
				}
				player.x += 10;
			}
			//upper collision
			if(upperSide >= player.getY()){
				player.y -= 5;
			}
			//lower collision
			if(lowerSide <= player.getY() + player.getHeight()){
				player.y += 5;
			}
		}
	}	
	
	// Determines what happens when two different creatures collide.
	// Uncommenting the onSreen condition makes this more efficient, but more buggy
	public void creatureCollision(Creature creature) {	
		/**
		 * Handle collisions for specific types of creatures here
		 * Sample code below
		 * 
		 */
		/*
		if(!this.isItem && !creature.isItem && !this.isPlatform && !creature.isPlatform &&
				this != creature && this.isCollidable() && creature.isCollidable()) {
			
			boolean collision = isCollision(this, creature);
			if(collision) {	
				// Handeling RedShell collision cases....
				// ______________________________________
				// creature 1 is a RedShell, creature 2 is not.
				if(this instanceof RedShell && !(creature instanceof RedShell)) {
					if(((RedShell) this).isMoving()) {
						creature.flip();
						soundManager.playKick();
					}
				// creature 2 is a RedShell, creature 1 is not.
				} else if(!(this instanceof RedShell) && creature instanceof RedShell) {
					if(((RedShell) creature).isMoving()) {
						this.flip();
					}
				// both creature 1 and creature 2 are RedShells
				} else if(this instanceof RedShell && creature instanceof RedShell) {
					//RedShell 1 is moving, RedShell 2 is not.
					this.flip();
					creature.flip();
				// End of RedShell collision cases...
			    //____________________________________________
					
			    } else {
			    	this.creatureXCollide();
			    	creature.creatureXCollide();
			    }
				
				*/
	}
}
