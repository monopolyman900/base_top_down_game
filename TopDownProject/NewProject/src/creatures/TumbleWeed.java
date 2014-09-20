package creatures;

import java.awt.image.BufferedImage;
import java.util.Random;

import tile.TileMap;
import animation.Animation;
import utilities.ImageManipulator;
import base.Creature;


public class TumbleWeed extends Creature {
	
	private String ID;
	private final float TERMINAL_WIND_SPEED = (float).05;
	
	private static BufferedImage[] c = { ImageManipulator.loadImage("items/tumbleWeed_1.png"), ImageManipulator.loadImage("items/tumbleWeed_2.png"),
		ImageManipulator.loadImage("items/tumbleWeed_3.png"), ImageManipulator.loadImage("items/tumbleWeed_4.png") };
	public static Animation tumble = new Animation(1500).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
	
	public TumbleWeed(int pixelX, int pixelY, String ID) {
		
		super(pixelX, pixelY, ID);
		setIsCollidable(true);
		
		this.setdX((float).05);
		
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		setAnimation(tumble);

	}
	
	public void updateCreature(TileMap map, int time) {
		if(currentAnimation() == tumble) {
			super.update(time);
			Random r = new Random();
			super.wakeUp();
			if(dx > TERMINAL_WIND_SPEED && dx > 0){
				dx = dx - (float).01;
			}
			else{
				if(dx < -TERMINAL_WIND_SPEED){
					dx = dx + (float).1;
				}
				else{
					if(dx < TERMINAL_WIND_SPEED){
						this.dx += ((float).000001 + this.dx);
					}
				}
			}
			if(dy > 0){
				dy -= .01;
			}
			else{
				if(dy < 0){
					dy += .01;
				}
			}
			x = x + time*dx;
			y = y + time*dy;
		}
	}	
}
