package player;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import menus.Inventory;
import animation.Animation;
import animation.SingleAnimation;
import creatures.Platform;
import creatures.TumbleWeed;
import creatures.Zombie;
import core.GameRenderer;
import tile.Chest;
import tile.GameTile;
import tile.Tile;
import base.Collision;
import base.Creature;
import base.Item;
import base.Weapon;
import tile.TileMap;
import utilities.ImageManipulator;
import animation.CollidableObject;

/**
 * The player is the main object in the game and is the center of the screen and attention at all
 * time. As a result, he is also the most complicated object in terms of animation, collision detection,
 * user input etc. 
 */

public class Player extends CollidableObject{

	/* Static Constant Fields.
	 * Gravity:   Effects the amount of pull objects feel toward the ground. pixels/ms
	 * Friction:  Effects the amount of sliding an object displays before coming to a stop.
	 * S_X:       Starting X position of Mario.
	 * S_Y:       Starting Y position of Mario.
	 * S_DY:      Starting Dy of Mario.
	 * S_JH:      Effects the height of Mario's first jump.
	 * Anim_Time: The time between each of Mario's Animations. 
	 * 
	 * Terminal_Walking_Dx:  Max speed when Mario is walking.
	 * Terminal_R3unning_Dx:  Max speed when Mario is running.
	 * Terminal_Fall_Dy:     Max speed Mario can fall at.
	 * Walking_Dx_Inc:       The increase in speed per update when walking until terminal runnning is reached.
	 * Running_Dx_Inc:       The increase in speed per update when running until terminal walking is reached.
	 * Start_Run_Anim_Thres: The speed where mario switches to the running animation.
	 */
	
	/*public static final float GRAVITY = 0.0008f;
	public static final float FRICTION = 0.0004f;                   
	private static final int STARTING_X = 25;
	private static final int STARTING_Y = 140;
	private static final float STARTING_DY = .03f;
	private static final float INITIAL_JUMP_HEIGHT = -.34f; 
	private static final float JUMP_MULTIPLIER = .46f;
	private static final float TERMINAL_WALKING_DX = .10f;
	private static final float WALKING_DX_INC = .01f;
	private static final float TERMINAL_RUNNING_DX = .21f;
	private static final float START_RUN_ANIM_THRESHOLD = .2f;
	private static final float RUNNING_DX_INC = .001f;
	private static final float TERMINAL_FALL_DY = .22f;
	private static final int STARTING_LIFE = 1;
	private static final int ANIM_TIME = 125;			*/
	
	String newLine = System.getProperty("line.separator");//This will retrieve line separator dependent on OS.
	
	public static final float GRAVITY = 0.0000f;
	public static final float FRICTION = 0.0008f;
	private static final float STARTING_DY = .00f;
	private static final float INITIAL_JUMP_HEIGHT = -.34f; 
	private static final float JUMP_MULTIPLIER = .46f;
	private static final float TERMINAL_WALKING_DX = .70f;//.30f; -- this is a good speed (but not for testing)
	private static final float TERMINAL_WALKING_DY = .70f;//.30f; -- this is a good speed (but not for testing)
	private static final float WALKING_DX_INC = .05f;
	private static final float WALKING_DY_INC = .05f;
	private static final float TERMINAL_RUNNING_DX = .30f;
	private static final float TERMINAL_RUNNING_DY = .30f;
	private static final float START_RUN_ANIM_THRESHOLD = .2f;
	private static final float RUNNING_DX_INC = .001f;
	private static final float RUNNING_DY_INC = .001f;
	//private static final float TERMINAL_FALL_DY = .22f;
	private static final int STARTING_LIFE = 1;
	private static final int ANIM_TIME = 250;
	private static final int ACTIONTILE_DISTANCE = 15;
	
	private int attackDistance = 15;
	
	ArrayList<GameTile> actionTiles;
	ArrayList<Creature> nearCreatures;
	
	/* INITIAL_JUMP_HEIGHT + dx*JUMP_MULTIPLIER */
	private float jumpHeight; 
	
	/* Boolean variables used to identify which keys are pressed. */
	private boolean isDownHeld, isUpHeld, isRightHeld, isLeftHeld, isShiftHeld, isSpaceHeld, isAHeld;
	/* Boolean variables used to identify where player is with respect to Platforms. */
	private boolean isRightOfPlatform, isLeftOfPlatform, isBelowPlatform, isAbovePlatform;
	/* Boolean variables used to identify where player is with respect to Slopes. */
	private boolean isUpSlope, isDownSlope, onSlopedTile;
	/* Boolean variables used to identify the state of player. */
	private boolean isJumping, frictionLock, isInvisible;
	
	public boolean isIndoors = false;
	public boolean attacking;
	
	public Inventory inventory;
	
	/* Animation variables. */
	private Animation walkLeft, runLeft, stillLeft, jumpLeft, crouchLeft, changeLeft, currLeftAnim;
	private Animation walkRight, runRight, stillRight, jumpRight, crouchRight, changeRight, currRightAnim;
	private Animation walkUp, runUp, stillUp, currUpAnim;
	private Animation walkDown, runDown, stillDown, currDownAnim;
	
	private SingleAnimation attackLeft, attackRight, attackUp, attackDown;
	
	private int health;
	private int grace;
	private Platform platform;
	
	private int actionX, actionY;
	private float[] actionPoint = new float[2];
	public double damageLevel;
	public double playerDamageLevel = 1;
	
	String direction;
	
	//chest used for linking chest inventory display and key bindings
	public Chest chestInv;
	public int chestInvNumber;
	
	
	private static String ID = "player";
	
	public Player(int startingX, int startingY){
		
		super(startingX, startingY, ID);
		
		setIsJumping(false);
		dy = STARTING_DY;
		jumpHeight = INITIAL_JUMP_HEIGHT;
		health = STARTING_LIFE;
		
		BufferedImage[] l = { ImageManipulator.loadImage("player/player_facing_left.png"), ImageManipulator.loadImage("player/player_walking_left1.png"),		//0
				ImageManipulator.loadImage("player/player_walking_left2.png"), ImageManipulator.loadImage("player/player_walking_left3.png"),
				ImageManipulator.loadImage("player/player_walking_left4.png"), ImageManipulator.loadImage("player/player_walking_left1.png"), 
				ImageManipulator.loadImage("player/player_walking_left2.png"), ImageManipulator.loadImage("player/player_walking_left3.png"), 					//6
				ImageManipulator.loadImage("player/player_walking_left4.png"), ImageManipulator.loadImage("player/player_walking_up1.png"), 
				ImageManipulator.loadImage("player/player_walking_up2.png"), ImageManipulator.loadImage("player/player_facing_up.png"), 
				ImageManipulator.loadImage("player/player_walking_down1.png"), ImageManipulator.loadImage("player/player_walking_down2.png"), 					//12
				ImageManipulator.loadImage("player/player_facing_down.png"), ImageManipulator.loadImage("player_attacking/player_down_axe1.png"), 
				ImageManipulator.loadImage("player_attacking/player_down_axe2.png"), ImageManipulator.loadImage("player_attacking/player_down_axe3.png"), 
				ImageManipulator.loadImage("player_attacking/player_down_axe4.png"), ImageManipulator.loadImage("player_attacking/player_up_axe1.png"),			//18
				ImageManipulator.loadImage("player_attacking/player_up_axe2.png"), ImageManipulator.loadImage("player_attacking/player_up_axe3.png"), 
				ImageManipulator.loadImage("player_attacking/player_up_axe4.png"), ImageManipulator.loadImage("player_attacking/player_left_axe1.png"), 
				ImageManipulator.loadImage("player_attacking/player_left_axe2.png"), ImageManipulator.loadImage("player_attacking/player_left_axe3.png"), 		//24
				ImageManipulator.loadImage("player_attacking/player_left_axe4.png"),  ImageManipulator.loadImage("player_attacking/player_right_axe1.png"), 
				ImageManipulator.loadImage("player_attacking/player_right_axe2.png"), ImageManipulator.loadImage("player_attacking/player_right_axe3.png"), 
				ImageManipulator.loadImage("player_attacking/player_right_axe4.png")};																			//30
		
		BufferedImage[] r = { null, null, null, null, null, null, null, null, null, null,
							  null, null, null, null, null, null, null, null, null, null, 
							  null, null, null, null, null, null, null, null, null, null,
							  null};
		for(int i = 0; i < l.length; i++) {
			r[i] = ImageManipulator.horizontalFlip(l[i]); // Flip every image in l.
		}
				
		// Create left animations.
		stillLeft = new Animation(ANIM_TIME).addFrame(l[0]);
		walkLeft = new Animation(ANIM_TIME).addFrame(l[1]).addFrame(l[2]).addFrame(l[3]).addFrame(l[4]);
		runLeft = new Animation(ANIM_TIME).addFrame(l[1]).addFrame(l[2]).addFrame(l[3]).addFrame(l[4]);
		crouchLeft = new Animation(ANIM_TIME).addFrame(l[5]);
		jumpLeft = new Animation(ANIM_TIME).addFrame(l[6]);
		changeLeft = new Animation(ANIM_TIME).addFrame(l[7]);
		
		// Create right animations.
		stillRight = new Animation(ANIM_TIME).addFrame(r[0]);
		walkRight = new Animation(ANIM_TIME).addFrame(r[1]).addFrame(r[2]).addFrame(r[3]).addFrame(r[4]);
		runRight = new Animation(ANIM_TIME).addFrame(r[1]).addFrame(r[2]).addFrame(r[3]).addFrame(r[4]);
		crouchRight = new Animation(ANIM_TIME).addFrame(r[5]);
		jumpRight = new Animation(ANIM_TIME).addFrame(r[6]);
		changeRight = new Animation(ANIM_TIME).addFrame(r[7]);
		
		//Create up animations
		stillUp = new Animation(ANIM_TIME).addFrame(l[11]);
		walkUp = new Animation(ANIM_TIME).addFrame(l[9]).addFrame(l[10]);
		runUp = new Animation(ANIM_TIME).addFrame(l[9]).addFrame(l[10]);
		
		//Create down animations
		stillDown = new Animation(ANIM_TIME).addFrame(l[14]);
		walkDown = new Animation(ANIM_TIME).addFrame(l[12]).addFrame(l[13]);
		runDown = new Animation(ANIM_TIME).addFrame(l[12]).addFrame(l[13]);
		
		attackLeft = new SingleAnimation(ANIM_TIME, stillLeft, this).addFrame(l[23]).addFrame(l[24]).addFrame(l[25]).addFrame(l[26]);
		attackRight = new SingleAnimation(ANIM_TIME, stillRight, this).addFrame(l[27]).addFrame(l[28]).addFrame(l[29]).addFrame(l[30]);
		attackUp = new SingleAnimation(ANIM_TIME, stillUp, this).addFrame(l[19]).addFrame(l[20]).addFrame(l[21]).addFrame(l[22]);
		attackDown = new SingleAnimation(ANIM_TIME, stillDown, this).addFrame(l[15]).addFrame(l[16]).addFrame(l[17]).addFrame(l[18]);
		
		setAnimation(stillRight);
		currLeftAnim = walkLeft;
		currRightAnim = walkRight;
		currUpAnim = walkUp;
		currDownAnim = walkDown;
		
		//initialize inventory menu
		inventory = new Inventory();
		inventory.setMenuTitle("Player Inventory");
		
     	//////////////test for inventory
		inventory.inventoryItems.add(new Item("Item1"));
		inventory.inventoryItems.add(new Item("Item2"));
		inventory.inventoryItems.add(new Item("Item3"));
		inventory.inventoryItems.add(new Item("Item4"));
		inventory.inventoryItems.add(new Item("Item5"));
		inventory.inventoryItems.add(new Item("Item6"));
		inventory.inventoryItems.add(new Item("Item7"));
		inventory.inventoryItems.add(new Item("Item8"));
		inventory.inventoryItems.add(new Item("Item9"));
		inventory.inventoryItems.add(new Item("Item10"));
		inventory.inventoryItems.add(new Item("Item11"));
		inventory.inventoryItems.add(new Item("Item12"));
		inventory.inventoryItems.add(new Item("Item13"));
		inventory.inventoryItems.add(new Item("Item14"));
		inventory.inventoryItems.add(new Item("Item15"));
		inventory.inventoryItems.add(new Item("Item16"));
		inventory.inventoryItems.add(new Item("Item17"));
		inventory.inventoryItems.add(new Item("Item18"));
		inventory.inventoryItems.add(new Item("Item19"));
		inventory.inventoryItems.add(new Item("Item20"));
		inventory.inventoryItems.add(new Item("Item21"));
		inventory.inventoryItems.add(new Item("Item22"));
		inventory.inventoryItems.add(new Item("Item23"));
		inventory.inventoryItems.add(new Item("Item24"));
		inventory.inventoryItems.add(new Item("Item25"));
		inventory.inventoryItems.add(new Item("Item26"));
		inventory.inventoryItems.add(new Item("Item27"));
		inventory.inventoryItems.add(new Item("Item28"));
		inventory.inventoryItems.add(new Item("Item29"));
		inventory.inventoryItems.add(new Item("Item30"));		
		
	}
	
	public int getHealth() {
		return health;
	}
	
	public Inventory getInventory(){
		return inventory;
	}
	
	public boolean isInvisible() {
		return isInvisible;
	}
	
	public boolean isOnSlopedTile() { return onSlopedTile; }

	public void setIsJumping(boolean isJumping) { this.isJumping = false;}//isJumping; }

	public boolean isJumping() { return isJumping; }
	
	public boolean isIndoors(){ return isIndoors; }
	
	private void slowSpeedX(int slowFactor) { setdX(getdX()/slowFactor);}
	
	private void slowSpeedY(int slowFactor) { setdY(getdY()/slowFactor);}
	
	public boolean isAbovePlatform() { return isAbovePlatform; }
	
	public float[] getActionPoint(){
		return actionPoint;
	}
	
	//used to activate action tiles
	public void setActionPoint(float x, float y){
		actionPoint[0] = x;
		actionPoint[1] = y;
	}
	
	public double getDamageLevel(){
		return damageLevel;
	}
	
	public void setDamageLevel(){
		if(getInventory().getEquippedWeapon() != null){
			damageLevel = (getInventory().getEquippedWeapon().getDamageLevel())+playerDamageLevel;
		}
		else{
			damageLevel = playerDamageLevel;
		}
	}
	
	public void displayChestInv(Chest chest, int chestNumber){
		this.chestInv = chest;
		this.chestInvNumber = chestNumber;
	}
	
	/**
	 * Debugging method used to print the status of mario with regards to platforms.
	 */
	private void platformStatus() {
		
		if(isAbovePlatform) { System.out.println("Above a platform"); }
		if(isBelowPlatform) { System.out.println("Below a platform"); }
		if(isLeftOfPlatform) { System.out.println("Left of a platform"); }
		if(isRightOfPlatform) { System.out.println("Right of a platform"); }
	}
	
	/**
	 * Fixes Y movement on tiles and platforms where animation height changes by setting the mario's y
	 * value to the difference between animation heights. 
	 */
	public void setAnimation(Animation newAnim) {
		if(currentAnimation() != null) {
			Animation currAnim = currentAnimation();
			int oldHeight = currAnim.getHeight();
			int newHeight = newAnim.getHeight();
			if(newHeight > oldHeight) {
				setY(getY() - (newHeight - oldHeight));	
			} else if(oldHeight > newHeight) {
				setY(getY() + oldHeight - newHeight);
			}
		}
		super.setAnimation(newAnim);
	}
	
	/**
	 * Given the float parameter oldX, oldY, newX, and newY this method returns the first 
	 * Platform in the TileMap map in which the sprite with the given parameters collides with
	 * in the X direction, if any. 
	 * 
	 * Note to self: the exact conditions for a collision are a little fuzzy....document this...
	 * 
	 * @modifies isLeftOfPlatform && isRightOfPlatform
	 * @return the Platform the sprite with the given parameters is colliding with in the X
	 * direction.
	 */
	private Platform getPlatformCollisionX(TileMap map, float oldX, float oldY, float newX, float newY) {
		
		for(Platform platform : map.platforms()) {
	    	float width = getWidth();
	    	float height = getHeight();
	    	float pX = platform.getX();
	    	float pY = platform.getY();
	    	float oldpX = platform.getOldX();
	    	float pWidth = platform.getWidth();
	    	float pHeight = platform.getHeight();
	    	
	    	if(oldX + width <= oldpX && // This is needed to make transparant platforms work
		       !platform.canJumpThrough() &&
	    	   newX + width >= pX && newX + width <= pX + pWidth &&
	    	   pY + pHeight > oldY && pY < oldY + height
	    	   ) {
	    		this.isLeftOfPlatform = true;
	    		this.isRightOfPlatform = false;
	    		return platform;
	    	} else if (
	    	   oldX >= oldpX + pWidth && // This is needed to make transparant platforms work
	    	   !platform.canJumpThrough() &&
	    	   newX <= pX + pWidth && newX >= pX &&
	    	   pY + pHeight > oldY && pY < oldY + height
	    	   ) {
	    		this.isRightOfPlatform = true;
	    		this.isLeftOfPlatform = false;
	    		return platform;
	    	}
	    }
	    this.isRightOfPlatform = false;
	    this.isLeftOfPlatform = false;
	    return null;
	}
	
	/**
	 * Given the float parameter oldX, oldY, newX, and newY this method returns the first 
	 * Platform in the TileMap map in which the sprite with the given parameters collides with
	 * in the Y direction, if any. 
	 * 
	 * Note to self: the exact conditions for a collision are a little fuzzy....document this...
	 * Vertically moving platforms aren't compatible with tiles, so don't use them in conjunction.
	 * 
	 * @modifies isLeftOfPlatform && isRightOfPlatform
	 * @return the Platform the sprite with the given parameters is colliding with in the X
	 * direction.
	 */
	private Platform getPlatformCollisionY(TileMap map, float oldX, float oldY, float newX, float newY) {
		for(Platform platform : map.platforms()) {
	    	float width = getWidth();
	    	float height = getHeight();
	    	float pX = platform.getX();
	    	float pY = platform.getY();
	    	float oldpY = platform.getOldY();
	    	float pWidth = platform.getWidth();
	    	float pHeight = platform.getHeight(); 
	    	
	    	if(//oldY + height <= pY && // This line makes vertical platforms buggy.
	    	   newY + height >= pY &&
	    	   newY + height <= pY + pHeight &&
	    	   oldX + width >= pX &&
	    	   oldX <= pX + pWidth &&
	    	   oldY + height <= oldpY) { 
		    	this.isAbovePlatform = true;
		    	this.isBelowPlatform = false;
		    	return platform;
	    	} else if(!platform.canJumpThrough()) {
	    	   if (oldY >= oldpY + pHeight && 
	    		   newY <= pY + pHeight &&
	    		   newY >= pY &&
		           oldX + width >= pX &&
		           oldX <= pX + pWidth) {
		        	   this.isBelowPlatform = true;
		        	   this.isAbovePlatform = false;
		        	   return platform;
		           }
	    	}
	    }
	    this.isBelowPlatform = false;
	    this.isAbovePlatform = false;
	    return null;
	}
	
	public void update(TileMap map, float time) {
		
		jumpHeight = INITIAL_JUMP_HEIGHT - Math.abs(dx)*JUMP_MULTIPLIER; 
		//walking
		if (isLeftHeld && !isShiftHeld) {
			//walking left
			//toggleMovement(1);
			if (dx < -TERMINAL_WALKING_DX) {
				setAnimation(currLeftAnim);
				dx = dx + WALKING_DX_INC;
			} else if (dx > -TERMINAL_WALKING_DX) {
				dx = dx - WALKING_DX_INC;
			}
		} else if (isRightHeld && !isShiftHeld) {
			//walking right
			//toggleMovement(1);
			if (dx > TERMINAL_WALKING_DX) {
				setAnimation(currRightAnim);
				dx = dx - WALKING_DX_INC;
			} else if(dx < TERMINAL_WALKING_DX) {
				dx = dx + WALKING_DX_INC;
			}
		} if (isDownHeld && !isShiftHeld) {
			//toggleMovement(1);
			//walking down
			if (dy > TERMINAL_WALKING_DY) {
				setAnimation(currDownAnim);
				dy = dy - WALKING_DY_INC;
			} else if(dy < TERMINAL_WALKING_DY) {
				dy = dy + WALKING_DY_INC;
			}
		} else if (isUpHeld && !isShiftHeld) {
			//toggleMovement(1);
			//walking up
			if (dy > -TERMINAL_WALKING_DY) {
				setAnimation(currUpAnim);
				dy = dy - WALKING_DY_INC;
			} else if(dy < -TERMINAL_WALKING_DY) {
				dy = dy + WALKING_DY_INC;
			}
		//Running
		} else if (isLeftHeld && isShiftHeld && !frictionLock) {
			//running left
			if (dx > -TERMINAL_WALKING_DX) {
				dx = dx - WALKING_DX_INC;
			} else if (dx > -TERMINAL_RUNNING_DX) {
				if (dx < -START_RUN_ANIM_THRESHOLD) {
					toggleMovement(2);
				}
				dx -= RUNNING_DX_INC;
			}
		} else if (isRightHeld && isShiftHeld && !frictionLock) {
			//running right
			if (dx < TERMINAL_WALKING_DX) {
				dx = dx + WALKING_DX_INC;
			}
			if (dx < TERMINAL_RUNNING_DX) {
				if (dx > START_RUN_ANIM_THRESHOLD) {
					toggleMovement(2);
				}
				dx += RUNNING_DX_INC;
			}
		} else {
			toggleMovement(1);
			if (dx != 0) {	
				frictionLock = true;
				if (dx > -.05f && dx < .05f) {
					dx = 0;
					frictionLock = false;
				} else if (dx > .005f) {
					dx = dx - FRICTION * time;
				} else if (dx < -.005f) {
					dx = dx + FRICTION * time;
				}
			}
			else{
				if (dy != 0) {
					frictionLock = true;
					if (dy > -.05f && dy < .05f) {
						dy = 0;
						frictionLock = false;
					} else if (dy > .005f) {
						dy = dy - FRICTION * time;
					} else if (dy < -.005f) {
						dy = dy + FRICTION * time;
					}
				}
			}
		}
	
		boolean lastFour = grace%8 == 7 || grace%8 == 6 || grace%8 == 5 || grace%8 == 4;
		isInvisible = lastFour ? true : false;
		if(grace != 0) { grace--; }
		
		// Slowly reset offset values caused by slopes.
		if(getOffsetX() != 0) { setOffsetX(getOffsetX() - 1);} 

		// Calculate the new X position.
		float oldX = getX();
		float newXCalc = oldX + getdX()*time;
		if(platform != null) { newXCalc += platform.getdX() * time; } 
		// Calculate the new Y position.
		float oldY = getY();
		float newYCalc = oldY + getdY()*time;
		//if(platform != null) { newYCalc = platform.getY() - getHeight(); }
		
		//get collision, floormat, and threshold points.
		//X
		ArrayList<ArrayList<Point>> allPointsX = GameRenderer.getTileCollisionAll(map, this, getX(), getY(), newXCalc, getY());
		ArrayList<Point> xTile = allPointsX.get(0);
		ArrayList<Point> xTileF = allPointsX.get(1);
		ArrayList<Point> xTileT = allPointsX.get(2);
		//Y
		ArrayList<ArrayList<Point>> allPointsY = GameRenderer.getTileCollisionAll(map, this, getX(), getY(), getX(), newYCalc);
		ArrayList<Point> yTile = allPointsY.get(0);
		ArrayList<Point> yTileF = allPointsY.get(1);
		ArrayList<Point> yTileT = allPointsY.get(2);
		//Numbers of points
		int numOfXTiles = xTile.size();
		int numOfYTiles = yTile.size();
		int numOfXTilesF = xTileF.size();
		int numOfYTilesF = yTileF.size();
		int numOfXTilesT = xTileT.size();
		int numOfYTilesT = yTileT.size();
		
		Platform platformX = getPlatformCollisionX(map, oldX, oldY, newXCalc, newYCalc);
		Platform platformY = getPlatformCollisionY(map, oldX, oldY, newXCalc, newYCalc);
		
		//this.platformStatus();
		
		if(isAbovePlatform) {
			platform = platformY;
		} else {
			platform = null;
		}
	
		// Manage collision in the X direction.
		if(oldX < 0) { // Collision with left side of map.
			setX(GameRenderer.tilesToPixels(0));
			slowSpeedX(20);
		} else if(oldX > GameRenderer.tilesToPixels(map.getWidth()) - 21) { // Collision with right side of map.
			setX(GameRenderer.tilesToPixels(map.getWidth()) - 21);
			slowSpeedX(20);
		} else {
			if(numOfXTiles == 0) { // No tile collision in the X direction
				setX(newXCalc);
			} else if(numOfXTiles >= 1) { // Tile collision in the X direction. For now, only worry
										  // about the first tile being collided with.
				
				Point xtp = xTile.get(0); // xTilePoint
				Collision c = Creature.tileCollisionX(map.getTile(xtp.x, xtp.y), this);
				toggleMovement(1);
				frictionLock = false;
				if(c == Collision.EAST) { // Left of a tile.
					setX(GameRenderer.tilesToPixels(xtp.x) - getWidth());
				} else if(c == Collision.WEST) { // Right of a tile.
					setX(GameRenderer.tilesToPixels(xtp.x + 1));
				}
				if(!isAbovePlatform) { setdX(0); } // Stop movement only if mario isn't on a Platform
												   // Why do this? If I don't mario gets frozen to tiles
												   // he X collides with while on a platform.
			}
			// Platform collision in X direction.
			if (platformX != null) { 
				slowSpeedX(2);
				if(isLeftOfPlatform) {
					setX(platformX.getX() - getWidth() - 1);
				} else if(isRightOfPlatform) {
					setX(platformX.getX() + platformX.getWidth() + 1);
				}
			} 
		}
		
		super.update((int) time); // Update mario's animation.
		
		// Manage collision in the Y direction. 
		boolean upperCollision = false; // will check if player is above a tile
			if(numOfYTiles == 0) {
				setY(newYCalc);
				setIsJumping(true);
				jump();
			} else if(numOfYTiles >= 1) { // Y collision detected with a tile 
				Point ytp = yTile.get(0); // yTilePoint
				Collision c = Creature.tileCollisionY(map.getTile(ytp.x, ytp.y), this);
				fixJumping();
				if(c == Collision.NORTH) { // Downward collision with tile.
					upperCollision = true;
					setIsJumping(false);
					setY(GameRenderer.tilesToPixels(ytp.y) - getHeight());
				} else if (c == Collision.SOUTH) { // Upward collision with tile.
					for(Point p : yTile) {
						GameTile tile = map.getTile(p.x, p.y);
						//if(tile != null) { tile.doAction(tile); }
					}
					setY(GameRenderer.tilesToPixels(ytp.y + 1));
				}
			}
			// Platform collision in Y direction.
			if (platformY != null && !upperCollision) { 
				fixJumping();
				if(isAbovePlatform) { // Downward collision with platform.
					setIsJumping(false);
					setY(platformY.getY() - getHeight());
				} else if (isBelowPlatform) { // Upward collision with platform.
					setY(platformY.getY() + platformY.getHeight() + 1);
					//accelerateFall(); 
				}
			}	
				
			// Manage collision in the X direction.
			if(oldX < 0) { // Collision with left side of map.
				setX(GameRenderer.tilesToPixels(0));
				//slowSpeedX(20);
			} else if(oldX > GameRenderer.tilesToPixels(map.getWidth()) - 21) { // Collision with right side of map.
				//setX(GameRenderer.tilesToPixels(map.getWidth()) - 21);
				//slowSpeedX(20);
			} else {
				if(numOfXTilesT == 0) { // No tile collision in the X direction
					//setX(newXCalc);
				} else if(numOfXTilesT >= 1) { // Tile collision in the X direction. For now, only worry
											  // about the first tile being collided with.
					
					Point xtp = xTileT.get(0); // xTilePoint
					Collision c = Creature.tileCollisionX(map.getTile(xtp.x, xtp.y), this);
					toggleMovement(1);
					frictionLock = false;
					if(c == Collision.EAST) { // Left of a tile.
						//setX(GameRenderer.tilesToPixels(xtp.x) - getWidth());
					} else if(c == Collision.WEST) { // Right of a tile.
						//setX(GameRenderer.tilesToPixels(xtp.x + 1));
					}
					if(!isAbovePlatform) { }//setdX(0); } // Stop movement only if mario isn't on a Platform
													   // Why do this? If I don't mario gets frozen to tiles
													   // he X collides with while on a platform.
				}
				// Platform collision in X direction.
				if (platformX != null) { 
					slowSpeedX(2);
					if(isLeftOfPlatform) {
						//setX(platformX.getX() - getWidth() - 1);
					} else if(isRightOfPlatform) {
						//setX(platformX.getX() + platformX.getWidth() + 1);
					}
				} 
			}
			
			super.update((int) time); // Update mario's animation.
			
			// Manage collision in the Y direction. 
			boolean upperCollision2 = false; // will check if mario is above a tile
				if(numOfYTilesT == 0) {
					//setY(newYCalc);
					setIsJumping(true);
					jump();
				} else if(numOfYTilesT >= 1) { // Y collision detected with a tile 
					Point ytp = yTileT.get(0); // yTilePoint
					Collision c = Creature.tileCollisionY(map.getTile(ytp.x, ytp.y), this);
					fixJumping();
					if(c == Collision.NORTH) { // Downward collision with tile.
						upperCollision2 = true;
						setIsJumping(false);
						//setY(GameRenderer.tilesToPixels(ytp.y) - getHeight()); 
					} else if (c == Collision.SOUTH) { // Upward collision with tile.
						for(Point p : yTileT) {
							this.isIndoors = true;
							GameTile tile = map.getTile(p.x, p.y);
							if(tile != null) { tile.doAction(tile); }
						}
					}
				}
				// Platform collision in Y direction.
				if (platformY != null && !upperCollision2) { 
					fixJumping();
					if(isAbovePlatform) { // Downward collision with platform.
						setIsJumping(false);
						//setY(platformY.getY() - getHeight());
					}
				}
			
				// Manage collision in the X direction.
				if(oldX < 0) { // Collision with left side of map.
					setX(GameRenderer.tilesToPixels(0));
					//slowSpeedX(20);
				} else if(oldX > GameRenderer.tilesToPixels(map.getWidth()) - 21) { // Collision with right side of map.
					//setX(GameRenderer.tilesToPixels(map.getWidth()) - 21);
					//slowSpeedX(20);
				} else {
					if(numOfXTilesF == 0) { // No tile collision in the X direction
						//setX(newXCalc);
					} else if(numOfXTilesF >= 1) { // Tile collision in the X direction. For now, only worry
												  // about the first tile being collided with.
						
						Point xtp = xTileF.get(0); // xTilePoint
						Collision c = Creature.tileCollisionX(map.getTile(xtp.x, xtp.y), this);
						toggleMovement(1);
						frictionLock = false;
						if(c == Collision.EAST) { // Left of a tile.
							//setX(GameRenderer.tilesToPixels(xtp.x) - getWidth());
						} else if(c == Collision.WEST) { // Right of a tile.
							//setX(GameRenderer.tilesToPixels(xtp.x + 1));
						}
						if(!isAbovePlatform) { }//setdX(0); } // Stop movement only if mario isn't on a Platform
														   // Why do this? If I don't mario gets frozen to tiles
														   // he X collides with while on a platform.
					}
					// Platform collision in X direction.
					if (platformX != null) { 
						slowSpeedX(2);
						if(isLeftOfPlatform) {
							//setX(platformX.getX() - getWidth() - 1);
						} else if(isRightOfPlatform) {
							//setX(platformX.getX() + platformX.getWidth() + 1);
						}
					} 
				}
				
				super.update((int) time); // Update mario's animation.
				
				// Manage collision in the Y direction. 
				boolean upperCollision3 = false; // will check if mario is above a tile
					if(numOfYTilesF == 0) {
						//setY(newYCalc);
						setIsJumping(true);
						jump();
					} else if(numOfYTilesF >= 1) { // Y collision detected with a tile 
						Point ytp = yTileF.get(0); // yTilePoint
						Collision c = Creature.tileCollisionY(map.getTile(ytp.x, ytp.y), this);
						fixJumping();
						if(c == Collision.NORTH) { // Downward collision with tile.
							upperCollision3 = true;
							setIsJumping(false);
								this.isIndoors = false;
						} else if (c == Collision.SOUTH) { // Upward collision with tile.
							for(Point p : yTileF) {
								////////////////////////////NEEDS TO BE REWORKED
									this.isIndoors = false;
								GameTile tile = map.getTile(p.x, p.y);
								if(tile != null) { tile.doAction(tile); }
							}
						}
					}
					// Platform collision in Y direction.
					if (platformY != null && !upperCollision3) { 
						fixJumping();
						if(isAbovePlatform) { // Downward collision with platform.
							setIsJumping(false);
							//setY(platformY.getY() - getHeight());
						}
					}
							
				//set actionCollision points
				Animation currentAnimation = super.getAnimation();
				if(currentAnimation == currLeftAnim || currentAnimation == stillLeft || currentAnimation == walkLeft){
					this.setActionPoint(this.x-ACTIONTILE_DISTANCE, this.y+(this.getHeight()/2));
				}
				if(currentAnimation == currRightAnim || currentAnimation == stillRight || currentAnimation == walkRight){
					this.setActionPoint(this.x+(ACTIONTILE_DISTANCE+this.getWidth()), this.y+(this.getHeight()/2));
				}
				if(currentAnimation == currUpAnim || currentAnimation == stillUp || currentAnimation == walkUp){
					this.setActionPoint((this.x+(this.getWidth()/2)), this.y-ACTIONTILE_DISTANCE);
				}
				if(currentAnimation == currDownAnim || currentAnimation == stillDown || currentAnimation == walkDown){
					this.setActionPoint((this.x+(this.getWidth()/2)), this.y+(ACTIONTILE_DISTANCE+this.getHeight()));
				}
				//get relevant actionTiles according to actionPoint
				actionTiles = GameRenderer.getActionTiles((int)actionPoint[0],((int) actionPoint[1]), map, this);				
				nearCreatures = GameRenderer.getNearCreatures(this, map);
				
		}
	
	// called from within marioUpdate() when mario hits a block while moving up.
	public void marioToTileToBaddieCollide(GameTile tile) {
		List<Creature> toRemove = new LinkedList<Creature>();
		for(Creature c : tile.collidingCreatures()) {
			if(c instanceof TumbleWeed) {
				c.flip();
				toRemove.add(c);
			}  			
		}
		for(Creature c : toRemove) { tile.collidingCreatures().remove(c); }
	}	
	
	// Determines what happens when player collides with a creature.
	public void playerCollision(TileMap map, Creature creature) {
		
		// only check collision of creatures with this that are not sleeping, are on the screen, and are collidable
		if(!creature.isPlatform() && creature.isCollidable()) { 
			boolean collision = isCollision(this, creature);
			if(collision) {
				
				if(creature instanceof TumbleWeed) {
					((TumbleWeed) creature).playerCollision((float)dx, (float)dy, this);
				}
				if(creature instanceof Zombie){
					//need to determine what happens here
					((Zombie) creature).playerCollision((float)dx, (float)dy, this);
				}
			
		/*		if(creature instanceof Coin) {
					creature.kill();
					map.creaturesToAdd().add(new Score(Math.round(creature.getX()), Math.round(creature.getY()+13)));
					
				}		*/
				/*else if(creature instanceof Mushroom) {
					soundManager2.playCelebrate();
					creature.kill();
					if(health == 3) {
					soundManager.playBonusPoints();
					map.creaturesToAdd().add(new Score(Math.round(creature.getX()), Math.round(creature.getY()+13)));
					} else {
						soundManager.playHealthUp();
						health++;
					}	
				} else if(creature instanceof Goomba && isJumping() && getdY() > 0) {
					((Goomba) creature).jumpedOn(); // kill goomba
					this.creatureHop();
					soundManager.playKick();
					
				} else if(creature instanceof RedKoopa && isJumping() && getdY() > 0) {
					((RedKoopa) creature).jumpedOn();
					creatureHop();
					soundManager.playKick();
					map.creaturesToAdd().add(new RedShell(Math.round(creature.getX()), 
							Math.round(creature.getY()+13), map, soundManager, true));
				} else if(creature instanceof RedShell) {
					
					if(this.isJumping() && this.getdY() > 0) {
						((RedShell) creature).jumpedOn(this.getdX() > 0, this.getdX());
						creatureHop();
						soundManager.playKick();	
						
					} else if(!isJumping() && !((RedShell) creature).isMoving()) {
						boolean right = (this.getdX() > 0);
						((RedShell) creature).jumpedOn(right, this.getdX());
						soundManager.playKick();
						// offset to avoid instant death, needed for sure
						if(right) {
							setX(this.getX() - 3);
						} else {
							setX(this.getX() + 3);
						}
					} else if(!isJumping()){
						getsDamaged();
					}
				} else {
					getsDamaged();
				}
			}	*/
			}
		}	
	}
	
	public void playerCollision(TileMap map, Item item){
		if(item.isCollidable()){
			boolean collision = isCollision(this, item);
			if(collision) {
				map.items().remove(item);
				inventory.addItem(item);
			}
		}
	}
	
	public void getsDamaged() {
		if(grace == 0) {
			health--;
			if(health <= 0) {
				if(health == 0) {
				}
			} else {
				grace = 80;
			}
		}
	}
	
	public void creatureHop() {
		isJumping = true;
		setY(y -5); // fix offset
		if(!isShiftHeld) {
			setdY(jumpHeight/2f); // jump
		} else {
			setdY(jumpHeight/1.4f);
		}
	}
	
	public void toggleMovement(int type) {

		if(type == 1) {
			currLeftAnim = walkLeft;
			currRightAnim = walkRight;
		}
		if(type == 2) {
			currLeftAnim = runLeft;
			currRightAnim = runRight;
		}
		if(type == 3) {
			currLeftAnim = stillLeft;
			currRightAnim = stillRight;
		}
	}
	
	public void fixJumping() {
		if(!isRightHeld && !isLeftHeld) {
			if(currentAnimation() == jumpLeft) {
				setAnimation(stillLeft);
				this.direction = "left";
			}
			if(currentAnimation() == jumpRight) {
				setAnimation(stillRight);
				this.direction = "right";
			}
		} else {
			if(!this.frictionLock) {
				if(isRightHeld) {
					setAnimation(currRightAnim);
					this.direction = "right";
				} else if (isLeftHeld) {
					setAnimation(currLeftAnim);
					this.direction = "left";
				}
			} else {
				//System.out.println("Do I ever get here");
				if(isRightHeld) {
					setAnimation(changeLeft);
					this.direction = "left";
				} else if (isLeftHeld) {
					setAnimation(changeRight);
					this.direction = "right";
				}
			}
		}
	}
	
	public void jump() {
		setIsJumping(false);
	}
	
	public void attack(){
		for(int i = 0; i < nearCreatures.size(); i++){
			if(!attacking){
				if(this.getAnimation() == currLeftAnim || this.getAnimation() == stillLeft){
					setAnimation(attackLeft);
				}
				else if(this.currentAnimation() == currRightAnim || this.currentAnimation() ==stillRight){
					setAnimation(attackRight);
				}
				else if(this.currentAnimation() == currDownAnim || this.currentAnimation() == stillDown){
					setAnimation(attackDown);
				}
				else if(this.currentAnimation() == currUpAnim || this.currentAnimation() == stillUp){
					setAnimation(attackUp);
				}
				Creature c = nearCreatures.get(i);
				if(c.isCollidable()){
					c.attacked(c, direction, damageLevel, this);
				}
			}
		}
	}
	
	public void actionButton(){
		
	}
	
	 public void keyPressed(KeyEvent e) {
	        int key = e.getKeyCode();

	        if (key == KeyEvent.VK_LEFT) {
	    		isLeftHeld = true;
	    			setAnimation(currLeftAnim);
	    			this.direction = "left";
	        }

	        if(key == KeyEvent.VK_RIGHT) {
	    		isRightHeld = true;
	    			setAnimation(currRightAnim);
	    			this.direction = "right";
	        }
	        
	        if(key == KeyEvent.VK_SHIFT) {
	        	this.isShiftHeld = true;
	        }
	      
	        
	        if(key == KeyEvent.VK_DOWN) {
	        	if(inventory.isDisplayed()){
        			inventory.moveDown();
	        	}
	        	else if(chestInv != null){
	        		if(chestInv.getInventory().isDisplayed()){
	        			chestInv.getInventory().moveDown();
	        		}
	        		else{
			        	isDownHeld = true;
			        	if(!isUpHeld){
			        		setAnimation(currDownAnim);
			        		this.direction = "down";
			        	}
	        		}
	        	}
	        	else{
		        	isDownHeld = true;
		        	if(!isUpHeld){
		        		setAnimation(currDownAnim);
		        		this.direction = "down";
		        	}
	        	}
	        }
	        
	        if(key == KeyEvent.VK_UP) {
	        	if(inventory.isDisplayed()){
        			inventory.moveUp();
	        	}
	        	else if(chestInv != null){
	        		if(chestInv.getInventory().isDisplayed()){
	        			chestInv.getInventory().moveUp();
	        		}
	        		else{
			        	isUpHeld = true;
			        	if(!isDownHeld){
			        		setAnimation(currUpAnim);
			        		this.direction = "up";
			        	}
	        		}
	        	}
	        	else{
		        	isUpHeld = true;
		        	if(!isDownHeld){
		        		setAnimation(currUpAnim);
		        		this.direction = "up";
		        	}
	        	}
	        }
	        
	        if(key == KeyEvent.VK_Z) {
	        	//action tiles activate only on release of z
	        }
	        
	        if(key == KeyEvent.VK_A) {
	        	if(!isAHeld && !attacking){
		        	//attack
		        	attack();
	        	}
	        	isAHeld = true;
	        	attacking = true;
	        }
	        
	        if(key == KeyEvent.VK_I) {
	        	if(!inventory.isDisplayed() && !GameRenderer.getMenuDisplayed()){
	        		//set first item to selected on initial display of inventory
	        		inventory.inventoryItems.get(0).setSelected(true);
	        		for(int i = 0; i < inventory.inventoryItems.size(); i++){
	        			if(i < 19){
	        				inventory.inventoryItems.get(i).setInventoryDisplayed(true);
	        			}
	        		}
	        		inventory.displayMenu();
	        	}
	        	else{
	        		inventory.hideMenu();
	        		//set none to selected
	        		for(int i = 0; i < inventory.inventoryItems.size(); i++){
	        			/*
	        			 * This stuff is for reinitializing purposes
	        			 */
	        			//set all items to not be inventory displayed
	        			//set all inventory items to not selected -- first one is set to selected outside the loop
	        			inventory.inventoryItems.get(i).setInventoryDisplayed(false);
	        			inventory.inventoryItems.get(i).setSelected(false);
	        		}
	        		//set only first item in inventory to displayed
	        		inventory.inventoryItems.get(0).setInventoryDisplayed(true);
	        	}
	        }
	        
	        if(key == KeyEvent.VK_SPACE) {	
	        	if(!isJumping && !isSpaceHeld) {
	        		isSpaceHeld = true;
	        	}
	        }
	        
	        //take contents from chest
	        if(key == KeyEvent.VK_ENTER) {	
	        	if(chestInv != null){
	        		if(chestInv.getInventory().isDisplayed() && chestInv.getInventory().getInventoryItems().size() > 0){
	        			Item selectedItem;
	        			int selectedItemIndex;
	        			selectedItem = chestInv.getInventory().getSelectedInventoryItem();
	        			selectedItemIndex = chestInv.getInventory().getSelectedItemIndex();
	        			
	        			//remove from chest, add to player inventory
		        		chestInv.getInventory().getInventoryItems().remove(selectedItem);
	        			inventory.addItem(selectedItem);
	        			selectedItem.setInventoryDisplayed(false);
	        			//logic to set the new selected inventory in the chest
		        		if(chestInv.getInventory().getInventoryItems().size() > 0){
		        			//if selected index is less than the inventory size - set next inventory to selected
		        			if(selectedItemIndex < chestInv.getInventory().getInventoryItems().size()){
		        				chestInv.getInventory().getInventoryItems().get(selectedItemIndex).setSelected(true);
		        			}
		        			else{
		        				//last item (in terms of index) in inventory was just taken
		        				chestInv.getInventory().getInventoryItems().get(selectedItemIndex-1).setSelected(true);	
		        			}
		        		}
		        		//chest is empty, update title
		        		else{
		        			chestInv.getInventory().setMenuTitle("Container Empty");
		        		}
	        		}
	        	}
	        }
	        //equip weapon
	        if(key == KeyEvent.VK_E) {	
	        	if(inventory.isDisplayed() && inventory.getSelectedInventoryItem() instanceof Weapon){
	        		if((Weapon)inventory.getSelectedInventoryItem() != this.getInventory().equippedWeapon){
	        			this.getInventory().equippedWeapon = (Weapon)inventory.getSelectedInventoryItem();
	        			setDamageLevel();
	        		}
	        		else{
	        			this.getInventory().equippedWeapon = null;
	        			setDamageLevel();
	        		}
	        	}
	        }
	    }
	    
	    public void keyReleased(KeyEvent e) {
	        int key = e.getKeyCode();

	        if (key == KeyEvent.VK_LEFT) {
	        	isLeftHeld = false;
	        	dx = 0;
	        	if(!isJumping) {
	        		setAnimation(stillLeft);
	        		this.direction = "left";
	        	}
	        	
	        }

	        if(key == KeyEvent.VK_RIGHT) {
	        	isRightHeld = false;
	        	dx = 0;
	        	if(!isJumping) {
	        		setAnimation(stillRight);
	        		this.direction = "right";
	        	}
	        }
	        
	        if(key == KeyEvent.VK_SHIFT) {
	        	this.isShiftHeld = false;
	        }
	        
	        // responsible for jumps of different heights
	        if(key == KeyEvent.VK_SPACE) {
	        	isSpaceHeld = false;
	        	dy = this.getdY()/2.5f;
	        }

	        if(key == KeyEvent.VK_DOWN) {
	        	isDownHeld = false;
	        	dy = 0;
	        	setAnimation(stillDown);
	        	this.direction = "down";
	        }
	        
	        if(key == KeyEvent.VK_UP) {
	        	isUpHeld = false;
	        	dy = 0;
	        	setAnimation(stillUp);
	        	this.direction = "up";
	        }
	        
	        if(key == KeyEvent.VK_Z) {
	        	if(actionTiles.size() > 0){
	        		for(int i = 0; i < actionTiles.size(); i++){
	        			//if the inventory menu is displayed, don't do action
	        			//mainly so we don't display 2 menus at once
	        			if(!getInventory().isDisplayed()){
		        			actionTiles.get(i).doAction(actionTiles.get(i));
	        			}
	        		}
	        	}
	        }
	        
	        if(key == KeyEvent.VK_A) {
	        	isAHeld = false;
	        	attacking = false;
	        }
	        
	        if(key == KeyEvent.VK_I) {
	        	//do nothing - inventory is displayed on press
	        }
	    }
}
