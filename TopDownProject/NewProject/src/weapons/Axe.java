package weapons;

import java.awt.image.BufferedImage;

import utilities.ImageManipulator;
import animation.Animation;
import base.Item;
import base.Weapon;

public class Axe extends Weapon{

	public static BufferedImage i = ImageManipulator.loadImage("weapons/axe.png");
	
	private static BufferedImage[] c = { ImageManipulator.loadImage("weapons/axe.png")};
	public static Animation axe = new Animation(1500).addFrame(c[0]);
	
	public Axe(int i, int j, String id) {
		super(i, j, id);
		setIsCollidable(true);
		setAnimation(axe);
		setDefaultAnim(axe);
		super.setDamageLevel(2.5);
	}
	
	public Axe(){
		this(0, 0, "axe");
	}
}
