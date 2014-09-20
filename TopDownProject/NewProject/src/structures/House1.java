package structures;

import java.awt.Color;
import java.awt.image.BufferedImage;

import utilities.ImageManipulator;
import animation.Animation;
import base.Structure;

public class House1 extends Structure{

	public static BufferedImage i = ImageManipulator.loadImage("structure/testHouse.png");
	
	private static BufferedImage[] c = { ImageManipulator.loadImage("structure/testHouse.png")};
	public static Animation tumble = new Animation(1500).addFrame(c[0]);
	
	public House1(int pixelX, int pixelY, String ID) {
		
		super(pixelX, pixelY, ID);
		setIsCollidable(true);
		setIsInpenetrable(true);
		
		getBoundaries();
		
		this.setdX((float).05);
		
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		setAnimation(tumble);

	}
	
	public BufferedImage getImage(){
		return i;
	}
	
	public void getBoundaries(){
		//needs to be fixed.
		int w, h;
		int bounds[][];
		for(int x = 0; x < i.getWidth(); x++){
			for(int y = 0; y < i.getHeight(); y++){
				
				int k = i.getRGB(x, y);
			    //System.out.println(x + " " + y + " " + k);
				int alpha = (k>>24) & 0xff;
				
				if(alpha == 255){
					
				}
			}
		}
	}
}
