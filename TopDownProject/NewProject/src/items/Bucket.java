package items;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import base.Item;
import utilities.ImageManipulator;
import animation.Animation;

public class Bucket extends Item{

	public static BufferedImage i = ImageManipulator.loadImage("items/bucket.png");
	
	private static BufferedImage[] c = { ImageManipulator.loadImage("items/bucket.png")};
	public static Animation bucket = new Animation(1500).addFrame(c[0]);
	
	public Bucket(int pixelX, int pixelY, String ID) {
		
		super(pixelX, pixelY, ID);
		setIsCollidable(true);
		setAnimation(bucket);
		setDefaultAnim(bucket);

	}
	
	public Bucket(){
		this(0, 0, "bucket");
	}
	
	public BufferedImage getImage(){
		return i;
	}
	
}
