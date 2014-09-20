package animation;

import java.awt.image.BufferedImage;

import animation.Animation.AnimFrame;

//this class is just used so that the animation doesn't loop 
//should behave exactly like animation besides this
public class SingleAnimation extends Animation{
	
	Animation defaultAnim;
	Sprite sprite;
	
	//We have to pass in a default animation and sprite here
	//this is so we can set the Sprite's animation after this singleAnimation is over
	public SingleAnimation(Animation defaultAnim, Sprite sprite){
		this.defaultAnim = defaultAnim;
		this.sprite = sprite;
	}
	
	public SingleAnimation(long defaultAnimLength, Animation defaultAnim, Sprite sprite){
		this.defaultAnim = defaultAnim;
		this.sprite = sprite;
		super.defaultAnimLength = defaultAnimLength;
	}
	
	@Override
	public void update(long elapsedTime){
		if(frames.size() > 1){		//there must be at least 2 frames to animate
			animTime += elapsedTime;
			
			if(animTime >= totalDuration){	//end
				animTime = 0;
				currFrameIndex = 0; //start over
				endOfAnimationAction();
				sprite.setAnimation(defaultAnim);
			}
			if(animTime > getFrame(currFrameIndex).endTime){
				currFrameIndex++;
			}
		}
	}
	
	/**
	 * Add a frame to the animation with the default animation length milliseconds.
	 * @modifies defaultAnimLength == duration.
	 * @returns this Animation with the added frame.
	 */
	public SingleAnimation addFrame(BufferedImage image){
		totalDuration += defaultAnimLength;
		frames.add(new AnimFrame(image, totalDuration));
		return this;
	}
	
	/**
	 * Add a frame to the animation with a given duration in milliseconds.
	 * @modifies defaultAnimLength == duration.
	 * @returns this Animation with the added frame.
	 */
	public SingleAnimation addFrame(BufferedImage image, long duration) {
		totalDuration += duration;
		frames.add(new AnimFrame(image, totalDuration));
		defaultAnimLength = duration;
		return this;
	}
	
}
