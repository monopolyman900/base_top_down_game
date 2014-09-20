package animation;

import java.awt.Graphics;
import java.io.Serializable;

/**
 * Animatible is an abstract class that a class should extend if it wants to be drawn using
 * an Animation. 
 */

public abstract class Animatable implements Serializable{
	
	private static final long serialVersionUID = 001L;

	private Animation currAnim;
	private int offsetX;
	private int offsetY;
	
	public abstract void draw(Graphics g, int pixelX, int pixelY);
	public abstract void draw(Graphics g, int pixelX, int pixelY, int offsetX, int offsetY);
	public abstract int getHeight();
	public abstract int getWidth();

	public Animation currentAnimation() {
		return currAnim;
	}
	
	public void setAnimation(Animation currAnim) {
		this.currAnim = currAnim;
	}
	
	public Animation getAnimation(){
		return currAnim;
	}
	
	public void update(int time) {
		currAnim.update(time);
	}
	
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}
	
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
}
