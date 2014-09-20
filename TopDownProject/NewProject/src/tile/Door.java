package tile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import utilities.ImageManipulator;
import animation.Animation;
import base.Creature;

public class Door extends ActionTile implements Serializable{
	
	private static final long serialVersionUID = 007L;

	private boolean isCollidable = false;
	private boolean isSloped = false;
	private boolean isThreshold = false;
	private boolean isFloormat = false;
	private List<Creature> collidingCreatures;
	
	private static final int ANIM_TIME = 125;
	
//	BufferedImage topDoorOpen;
//	BufferedImage topDoorClosed;
//	BufferedImage bottomDoorOpen;
//	BufferedImage bottomDoorClosed;
	
	transient BufferedImage doorOpen;
	transient BufferedImage doorClosed;
	
	Animation doorOpenAnim;
	Animation doorClosedAnim;
	
	private static String ID = "door";
	
	/**
	 * Constructs a new Door at the pixel (x,y) position with the Animation anim
	 * and Image img.
	 */

	public Door(int pixelX, int pixelY, Animation anim, BufferedImage img, boolean isCollidable) {
		super(pixelX, pixelY, anim, img, isCollidable, ID);
		collidingCreatures = new LinkedList<Creature>();
		setIsCollidable(isCollidable);
	}

	public Door(int pixelX, int pixelY, BufferedImage closed, BufferedImage open,
			String string, boolean b, boolean c, boolean d, boolean e) {
		super (pixelX, pixelY, closed, string, b, c, d, e);
		this.doorOpen = open;
		this.doorClosed = closed;
		doorOpenAnim = new Animation(ANIM_TIME).addFrame(doorOpen);
		doorClosedAnim = new Animation(ANIM_TIME).addFrame(doorClosed);
	}
	
	public Door(int pixelX, int pixelY, BufferedImage closed, BufferedImage open, String string){
		super (pixelX, pixelY, closed, string);
		this.doorOpen = open;
		this.doorClosed = closed;
		doorOpenAnim = new Animation(ANIM_TIME).addFrame(doorOpen);
		doorClosedAnim = new Animation(ANIM_TIME).addFrame(doorClosed);
	}
	
/*
	public Door(int pixelX, int pixelY, BufferedImage img, String ID) {
		this(pixelX, pixelY, img, false, ID, true, false, false, true);
	}
*/

	public void doAction() { 
		if(super.isCollidable()){
			super.setIsCollidable(false);
			super.setAnimation(doorOpenAnim);
		}
		else{
			super.setIsCollidable(true);
			super.setAnimation(doorClosedAnim);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		//ImageIO.write(this.getImage(), "png", out);
		ImageIO.write(this.doorOpen, "png", out);
		if(this.doorClosed != null)
		ImageIO.write(this.doorClosed, "png", out);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		//super.setImage(ImageIO.read(in));
		this.doorOpen = ImageIO.read(in);
		this.doorClosed = ImageIO.read(in);
	}
}
