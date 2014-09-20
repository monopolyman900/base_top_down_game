package creatures;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import core.GameRenderer;
import player.Player;
import animation.Animation;
import animation.Sprite;
import base.Collision;
import base.Creature;
import tile.GameTile;
import tile.TileMap;
import utilities.ImageManipulator;

public class Zombie extends Creature {
	
	private Animation walkLeft, walkRight, walkUp, walkDown, dead;
	private Animation stillLeft, stillRight, stillUp, stillDown;
	
	public double healthLevel = 6;
	
	private boolean frictionLock;
	
	private void slowSpeedX(int slowFactor) { setdX(getdX()/slowFactor);	System.out.println("collision");}
	private void slowSpeedY(int slowFactor) { setdY(getdY()/slowFactor);	System.out.println("collision");}

	
	Player player;
	
	private int attackCounter;
	boolean attacked;
	
	String ID;
	String direction;
	
	boolean wandering = true;
	boolean playerSpotted = false;
	
	int wanderCounter = 0;
	int wanderTimer;
	
	/*
	 * wanderTimer was initially 75
	 */
	//int wanderTimer = 75;
	
	public Zombie(int x, int y, String ID, Player player) {
		super(x, y, ID);
		
		BufferedImage wL1 = ImageManipulator.loadImage("baddies/zombie_walking_left_1.png");
		BufferedImage wL2 = ImageManipulator.loadImage("baddies/zombie_walking_left_2.png");
		BufferedImage wL3 = ImageManipulator.loadImage("baddies/zombie_walking_left_3.png");
		BufferedImage wL4 = ImageManipulator.loadImage("baddies/zombie_walking_left_4.png");	
		BufferedImage wR1 = ImageManipulator.loadImage("baddies/zombie_walking_right_1.png");
		BufferedImage wR2 = ImageManipulator.loadImage("baddies/zombie_walking_right_2.png");
		BufferedImage wR3 = ImageManipulator.loadImage("baddies/zombie_walking_right_3.png");
		BufferedImage wR4 = ImageManipulator.loadImage("baddies/zombie_walking_right_4.png");
		BufferedImage wU1 = ImageManipulator.loadImage("baddies/zombie_walking_up_1.png");
		BufferedImage wU2 = ImageManipulator.loadImage("baddies/zombie_walking_up_2.png");
		BufferedImage wD1 = ImageManipulator.loadImage("baddies/zombie_walking_down_1.png");
		BufferedImage wD2 = ImageManipulator.loadImage("baddies/zombie_walking_down_2.png");
		BufferedImage fL1 = ImageManipulator.loadImage("baddies/zombie_facing_left.png");
		BufferedImage fR1 = ImageManipulator.loadImage("baddies/zombie_facing_right.png");
		BufferedImage fU1 = ImageManipulator.loadImage("baddies/zombie_facing_up.png");
		BufferedImage fD1 = ImageManipulator.loadImage("baddies/zombie_facing_down.png");
		BufferedImage killedAnim1 = ImageManipulator.loadImage("baddies/zombie_facing_up.png");
		BufferedImage killedAnim2 = ImageManipulator.loadImage("baddies/zombie_facing_right.png");
		BufferedImage killedAnim3 = ImageManipulator.loadImage("baddies/zombie_facing_down.png");
		BufferedImage killedAnim4 = ImageManipulator.loadImage("baddies/zombie_facing_left.png");
		
		this.x = x;
		this.y = y;
		this.ID = ID;
		damageLevel = 2;
		
		wanderTimer = ((int)Math.round(Math.random()*100));
		if(wanderTimer < 15){
			wanderTimer += 20;
		}
		
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}

		walkLeft = new Animation(80).addFrame(wL1).addFrame(wL2).addFrame(wL3).addFrame(wL4);
		walkRight = new Animation(80).addFrame(wR1).addFrame(wR2).addFrame(wR3).addFrame(wR4);
		walkUp = new Animation(150).addFrame(wU1).addFrame(wU2);
		walkDown = new Animation(150).addFrame(wD1).addFrame(wD2);
		
		stillLeft = new Animation(150).addFrame(fL1);
		stillRight = new Animation(150).addFrame(fR1);
		stillUp = new Animation(150).addFrame(fU1);
		stillDown = new Animation(150).addFrame(fD1);
		
		//dead = new DeadAfterAnimation().addFrame(killedAnim).addFrame(killedAnim);
		dead = new DeadAfterAnimation().setDAL(100).addFrame(killedAnim1).setDAL(100).addFrame(killedAnim2).setDAL(100).addFrame(killedAnim3).setDAL(100).addFrame(killedAnim4).setDAL(100).addFrame(killedAnim1).setDAL(100).addFrame(killedAnim2).setDAL(100).addFrame(killedAnim3).setDAL(100).addFrame(killedAnim4);
		setAnimation(stillDown);
		super.setIsCollidable(true);
	}
	
	public Zombie(){
		this(0, 0, "zombie", null);
	}
	
	public void attacked(String direction, double damageLevel, Player player){
		this.player = player;
		playerSpotted = true;
		attacked = true;
		if(this.isAlive() && ((healthLevel - damageLevel) > 0)){
			healthLevel -= damageLevel;
			if(direction == "up"){
				this.dy = -2;
				this.dx = 0;
			}
			else if(direction == "down"){
				this.dy = 2;
				this.dx = 0;
			}
			else if(direction == "left"){
				this.dx = -2;
				this.dy = 0;
			}
			else if(direction == "right"){
				this.dx = 2;
				this.dy = 0;
			}
		}
		else{
			this.setAnimation(dead);
			this.setIsCollidable(false);
			this.dx = 0;
			this.dy = 0;
		}
	}
	
	public void wakeUp() {
		Random r = new Random();
		super.wakeUp();
		dx = (r.nextInt(3) == 0) ? -.03f : .03f;
	}
	
	public void jumpedOn() {
		setAnimation(dead);
		setIsCollidable(false);
		dx = 0;
	}
	
	public void playerCollision(){
		//System.out.println("zombieCollision");
	}
	
	public void wanderCounter(){
		if(wanderCounter%wanderTimer == 0){
			wanderCounter = 0;
			changeDirection();
		}
		wanderCounter++;	
	}
	
	public void chasePlayer(){
		if(abs(this.x - player.getX()) > abs(this.y - player.getY())){
			if(player.getX() < this.getX()){
				this.dx = -1;
				if(this.dy > 0){
					dy -= .03;
				}
				else{
					dy += .03;
				}
			}
			if(player.getX() > this.getX()){
				this.dx = 1;
				if(this.dy > 0){
					dy -= .03;
				}
				else{
					dy += .03;
				}
			}
		}
		else {
			if(player.getY() > this.getY()){
				this.dy = 1;
				if(this.dx > 0){
					dx -= .03;
				}
				else{
					dx += .03;
				}
			}
			if(player.getY() < this.getY()){
				this.dy = -1;
			}
			if(this.dx > 0){
				dx -= .03;
			}
			else{
				dx += .03;
			}
		}
	}
	
	public void changeDirection(){
		double xRand = Math.random();
		double yRand = Math.random();
		//coin is just a 50/50 chance used to move horizontally or vertically
		double coin = Math.random();
		double coin2 = Math.random();
		
		if(coin >= .25){
			//move horizontally
			if(coin2 >= .5){
				//move right
				if(xRand >= .5){
					direction = "right";
					dx = (float)0.5;
				}
				//move left
				else{
					direction = "left";
					dx = (float)-0.5;
				}
			}
			//move vertically
			else{
				//move down
				if(yRand >= .5){
					direction = "down";
					dy = (float)0.5;
				}
				//move up
				else{
					direction = "up";
					dy = (float)-0.5;
				}
			}
		}
		else{
			dy = 0;
			dx = 0;
			if(currentAnimation() == walkLeft){
				setAnimation(stillLeft);
			}
			else if(currentAnimation() == walkRight){
				setAnimation(stillRight);
			}
			else if(currentAnimation() == walkDown){
				setAnimation(stillDown);
			}
			else if(currentAnimation() == walkUp){
				setAnimation(stillUp);
			}
		}
	}
	
	public void checkPlayerDistance(){

	}
	
	public void manageCollisions(TileMap map, int time){
		// Calculate the new X position.
		float oldX = getX();
		float newXCalc = oldX + getdX()*time;
		// Calculate the new Y position.
		float oldY = getY();
		float newYCalc = oldY + getdY()*time;
		
		// Calculate all the tile collisions.
		ArrayList<ArrayList<Point>> xTileAll = GameRenderer.getTileCollisionAll(map, this, getX(), getY(), newXCalc, getY());
		ArrayList<ArrayList<Point>> yTileAll = GameRenderer.getTileCollisionAll(map, this, getX(), getY(), getX(), newYCalc);
		ArrayList<Point> xTile = xTileAll.get(0);
		ArrayList<Point> yTile = yTileAll.get(0);
		int numOfXTiles = xTile.size();
		int numOfYTiles = yTile.size();
		
		// Manage collision in the X direction.
		if((numOfXTiles > 0 && numOfYTiles == 0) || (numOfYTiles > 0 && numOfXTiles == 0)){
			if(oldX < 0) { // Collision with left side of map.
				setX(GameRenderer.tilesToPixels(0));
				slowSpeedX(20);
			} else if(oldX > GameRenderer.tilesToPixels(map.getWidth()) - 21) { // Collision with right side of map.
				setX(GameRenderer.tilesToPixels(map.getWidth()) - 21);
				slowSpeedX(20);
			} else {
				if(numOfXTiles == 0) { // No tile collision in the X direction
					//setX(newXCalc);
				} else if(numOfXTiles >= 1) { // Tile collision in the X direction. For now, only worry
											  // about the first tile being collided with.
					
					Point xtp = xTile.get(0); // xTilePoint
					Collision c = Creature.tileCollisionX(map.getTile(xtp.x, xtp.y), this);
					frictionLock = false;
					if(c == Collision.EAST) { // Left of a tile.
						setX(this.x - 1);
					} else if(c == Collision.WEST) { // Right of a tile.
						setX(this.x + 1);
					}
				}
			}
			// Manage collision in the Y direction.
			if(oldY < 0) { // Collision with left side of map.
				setY(GameRenderer.tilesToPixels(0));
				slowSpeedY(20);
			} else if(oldY > GameRenderer.tilesToPixels(map.getHeight()) - 21) { // Collision with right side of map.
				setY(GameRenderer.tilesToPixels(map.getHeight()) - 21);
				slowSpeedY(20);
			} else {
				if(numOfYTiles == 0) { // No tile collision in the Y direction
					//setY(newYCalc);
				} else if(numOfYTiles >= 1) { // Tile collision in the Y direction. For now, only worry
											  // about the first tile being collided with.
					
					Point ytp = yTile.get(0); // yTilePoint
					Collision c = Creature.tileCollisionY(map.getTile(ytp.x, ytp.y), this);
					//frictionLock = false;
					if(c == Collision.NORTH && numOfXTiles == 0) { // Above a tile.
						setY(this.y - 1);
					} else if(c == Collision.SOUTH && numOfXTiles == 0) { // Below a tile.
						setY(this.y + 1);
					}
				}
			}
		}
		else if(numOfXTiles > 0 && numOfYTiles > 0){
			//handle somehow...
		}
	}
	
	//I think this only happens when the creature is in frame.
	public void updateCreature(TileMap map, int time) {
		checkPlayerDistance();
		
		manageCollisions(map, time);
		
		super.update(time);
		if(dx > abs(2)){
			dx -= .2;
		}
		if(dy > abs(2)){
			dy -= .2;
		}
		//if attacked fall into different logic than wander
		if(attacked){
			if(attackCounter < 5){
				attackCounter++;
			}
			else{
				attackCounter = 0;
				attacked = false;
			}
		}
		else{
			//set wandering or targeting player
			if(this.isCollidable()){
				if(!playerSpotted){
					wanderCounter();
				}
				else{
					chasePlayer();
				}
			}
		}
		//set animations
		//moving horizontally
		if(abs(dx) > abs(dy)){
			//moving right
			if(dx > 0 && !attacked){
				setAnimation(walkRight);
			}
			//moving left
			else if(dx < 0 && !attacked){
				setAnimation(walkLeft);
			}
		//moving vertically
		}
		//moving down
		else if(dy > 0 && !attacked){
			setAnimation(walkDown);
			}
			//moving up
			else if(dy < 0 && !attacked){
				setAnimation(walkUp);
			}
			//standing still
			else{

			}
		//may need to be reworked.
		x = x + dx;
		y = y + dy;
	}
}