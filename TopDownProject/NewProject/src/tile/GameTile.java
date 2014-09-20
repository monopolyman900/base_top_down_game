package tile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import animation.Animation;
import base.Creature;

public class GameTile extends Tile implements Serializable{
	
	private static final long serialVersionUID = 011L;
	
	private boolean isCollidable = false;
	private boolean isSloped = false;
	private boolean isThreshold = false;
	private boolean isFloormat = false;
	private boolean isActionTile = false;
	private boolean isDoor;
	private boolean isChest;
	private List<Creature> collidingCreatures;
	public String ID;
	public GameTile secondaryTile;
	
	/**
	 * Constructs a new GameTile at the pixel (x,y) position with no Animation
	 * and the constant Image img.
	 */
	public GameTile(int pixelX, int pixelY, BufferedImage img, boolean isCollidable) {
		this(pixelX, pixelY, null, img, isCollidable);
	}
	
	/**
	 * Constructs a new GameTile at the pixel (x,y) position with the Animation anim
	 * and Image img.
	 */
	public GameTile(int pixelX, int pixelY, Animation anim, BufferedImage img, boolean isCollidable) {
		super(pixelX, pixelY, anim, img);
		collidingCreatures = new LinkedList<Creature>();
		setIsCollidable(isCollidable);
	}
	
	/**
	 * Constructs a new GameTile at the pixel (x,y) position with no Animation
	 * and the constant Image img.
	 */
	public GameTile(int pixelX, int pixelY, BufferedImage img, String ID, boolean isCollidable, boolean isThreshold, boolean isFloormat, boolean isActionTile) {
		this(pixelX, pixelY, null, img, ID, isCollidable, isThreshold, isFloormat, isActionTile);
	}
	
	public GameTile(int pixelX, int pixelY, Animation anim, BufferedImage img, String ID, boolean isCollidable, boolean isThreshold, boolean isFloormat, boolean isActionTile) {
		super(pixelX, pixelY, anim, img);
		collidingCreatures = new LinkedList<Creature>();
		setIsCollidable(isCollidable);
		this.isThreshold = isThreshold;
		this.isFloormat = isFloormat;
		this.isActionTile = isActionTile;
	}
	
	public GameTile(int pixelX, int pixelY, BufferedImage img) {
		this(pixelX, pixelY, img, false);
	}

	public GameTile(BufferedImage img){
		this(0, 0, img);
	}

	public GameTile(){
		this(null);
	}

	public void setAnimation(Animation animation){
		super.setAnimation(animation);
	}

	/**
	 * Override to add action to this GameTile.
	 */
	public void doAction(GameTile tile) {
		if(tile instanceof Door){
			((Door) tile).doAction(tile);
		}
	}
	
	/**
	 * @return true if this GameTile is collidable, else false.
	 */
	public boolean isCollidable() {
		return isCollidable;
	}
	
	/**
	 * @effects sets isCollidable to true or false.
	 */
	public void setIsCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}
	
	public boolean isThreshold() {
		return isThreshold;
	}
	
	public void setIsThreshold(boolean isThreshold){
		this.isThreshold = isThreshold;
	}
	
	public boolean isFloormat() {
		return isFloormat;
	}
	
	public void setIsFloormat(boolean isFloormat){
		this.isFloormat = isFloormat;
	}
	
	public boolean isActionTile(){
		return isActionTile;
	}
	
	public void setIsActionTile(boolean isActionTile){
		this.isActionTile = isActionTile;
	}
	
	public boolean isDoor(){
		return isDoor;
	}
	
	public void setIsDoor(boolean isDoor){
		this.isDoor = isDoor;
	}
	
	public boolean isChest(){
		return isChest;
	}
	
	public void setIsChest(boolean isChest){
		this.isChest = isChest;
	}
	
	public boolean isSloped() {
		return isSloped;
	}
	
	public void setIsSloped(boolean isSloped) {
		this.isSloped = isSloped;
	}
	
	public void setID(String ID){
		this.ID = ID;
	}
	
	public String getID(){
		return ID;
	}
	
	public GameTile getSecondaryTile(){
		return secondaryTile;
	}
	
	public void setSecondaryTile(GameTile secondaryTile){
		this.secondaryTile = secondaryTile;
	}

	/**
	 * @return a list of Creatures who are currently colliding with this GameTile.
	 */
	public List<Creature> collidingCreatures() {
		return collidingCreatures;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ImageIO.write(this.getImage(), "png", out);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		super.setImage(ImageIO.read(in));
	}
	
}
