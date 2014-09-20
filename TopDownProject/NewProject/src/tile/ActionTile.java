package tile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import animation.Animation;
import base.Creature;

public class ActionTile extends GameTile{

	private boolean isCollidable = false;
	private boolean isSloped = false;
	private boolean isThreshold = false;
	private boolean isFloormat = false;
	private List<Creature> collidingCreatures;
	private String ID;
	
	/**
	 * Constructs a new GameTile at the pixel (x,y) position with the Animation anim
	 * and Image img.
	 */
	public ActionTile(int pixelX, int pixelY, Animation anim, BufferedImage img, boolean isCollidable, String ID) {
		super(pixelX, pixelY, anim, img, isCollidable);
		collidingCreatures = new LinkedList<Creature>();
		this.setIsCollidable(isCollidable);
		super.setIsActionTile(true);
		this.ID = ID;
	}
	
	/**
	 * Constructs a new GameTile at the pixel (x,y) position with no Animation
	 * and the constant Image img.
	 */
	public ActionTile(int pixelX, int pixelY, BufferedImage img, boolean isCollidable, String ID) {
		super(pixelX, pixelY, null, img, isCollidable);
		this.ID = ID;
		this.setIsCollidable(isCollidable);
		super.setIsActionTile(true);
	}
	
	public ActionTile(int pixelX, int pixelY, BufferedImage img, String ID) {
		this(pixelX, pixelY, img, false, ID);
	}
	
	public ActionTile(int pixelX, int pixelY, BufferedImage bufferedImage,
			String string, boolean b, boolean c, boolean d, boolean e) {
		super (pixelX, pixelY, bufferedImage, string, b, c, d, e);
		super.setIsActionTile(true);
	}

	/**
	 * Override to add action to this GameTile.
	 */
	public void doAction(GameTile tile) {
		if(tile instanceof Door){
			Door door = ((Door) this);
			door.doAction();
		}
		else if(tile instanceof Chest){
			Chest chest = ((Chest) this);
			chest.doAction();
		}
	}
	
	/**
	 * @return true if this GameTile is collidable, else false.
	 */
	public boolean isCollidable() {
		return super.isCollidable();
	}
	
	/**
	 * @effects sets isCollidable to true or false.
	 */
	public void setIsCollidable(boolean isCollidable) {
		super.setIsCollidable(isCollidable);
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
	
	public boolean isSloped() {
		return isSloped;
	}
	
	public void setIsSloped(boolean isSloped) {
		this.isSloped = isSloped;
	}
	
	public void setAnimation(Animation animation){
		super.setAnimation(animation);
	}

	/**
	 * @return a list of Creatures who are currently colliding with this GameTile.
	 */
	public List<Creature> collidingCreatures() {
		return collidingCreatures;
	}
}
