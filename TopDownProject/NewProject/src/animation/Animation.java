package animation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;


/**
 * An object used to store an animation
 * 
 * An Animation object consists of many frames, each which occur for a given amount of time.
 * To play the animation, we update the frame by 'adding time'. The time is checked
 * vs the length of each frame. If current time > end time that the frame should
 * occupy, we update to the next frame. When the total duration of the animation is reached,
 * we go back to the first frame. This way we are continually moving through the frames!
 * When an animation is finished, it calls endOfAnimationAction(). This way you can perform
 * certain tasks once an animation is finished. 
 *
 */

public class Animation implements Serializable{
	
	private static final long serialVersionUID = 000L;

	protected ArrayList<AnimFrame> frames;
	protected int currFrameIndex;
	protected long animTime;
	protected long totalDuration;
	protected long defaultAnimLength;
	
	/**
	 * Constructs a new Animation object with no frames.
	 */
	public Animation(){
		frames = new ArrayList<AnimFrame>();
		totalDuration = 0;
		defaultAnimLength = 0;
		start();
	}
	
	/**
	 * Constructs a new Animation object with no frames and with the
	 * specified default animation length;
	 */
	public Animation(long defaultAnimLength){
		frames = new ArrayList<AnimFrame>();
		totalDuration = 0;
		this.defaultAnimLength = defaultAnimLength;
		start();
	}
	
	/**
	 * Sets the default animation length (DAL);
	 * @return this Animation with the default animation length changed.
	 */
	public Animation setDAL(long defaultAnimLength){
		this.defaultAnimLength = defaultAnimLength;
		return this;
	}
	
	/**
	 * Add a frame to the animation with the default animation length milliseconds.
	 * @modifies defaultAnimLength == duration.
	 * @returns this Animation with the added frame.
	 */
	public Animation addFrame(BufferedImage image){
		totalDuration += defaultAnimLength;
		frames.add(new AnimFrame(image, totalDuration));
		return this;
	}
	
	/**
	 * Add a frame to the animation with a given duration in milliseconds.
	 * @modifies defaultAnimLength == duration.
	 * @returns this Animation with the added frame.
	 */
	public Animation addFrame(BufferedImage image, long duration) {
		totalDuration += duration;
		frames.add(new AnimFrame(image, totalDuration));
		defaultAnimLength = duration;
		return this;
	}
	
	/**
	 * Starts this animation.
	 */
	public void start() {
		animTime = 0;
		currFrameIndex = 0;
	}
	
	/**
	 * @return the height of the current animation in pixels.
	 */
	public int getHeight() {
		return getFrame(currFrameIndex).image.getHeight(null);
	}
	
	/**
	 * @return the width of the current animation in pixels.
	 */
	public int getWidth() {
		return getFrame(currFrameIndex).image.getWidth(null);
	}
	
	public void update(long elapsedTime){
		if(frames.size() > 1){		//there must be at least 2 frames to animate
			animTime += elapsedTime;
			
			if(animTime >= totalDuration){	//reset animation
				animTime = 0;
				currFrameIndex = 0; //start over
				endOfAnimationAction();
			}
			if(animTime > getFrame(currFrameIndex).endTime){
				currFrameIndex++;
			}
		}
	}
	
    /**
     * Override this method to add an action when animation finishes its entire duration.
     */
    public void endOfAnimationAction() { }
	
    /**
     * @return an array of the images in this animation.
     * note: this method is never called.
     */
    private BufferedImage[] getImages() {
    	if(frames.size() == 0) {
    		return null;
    	} else {
    		return (BufferedImage[]) frames.toArray();
    	}
    }
	
    /**
     * @return the Image of the current frame of this animation.
     */
    public BufferedImage getImage() {
        if (frames.size() == 0) {
            return null;
        }
        else {
            return getFrame(currFrameIndex).image;
        }
    }
	
    /**
     * @return the AnimFrame of the given frame.
     * This is used in update to get the end time of the current frame.
     */
    protected AnimFrame getFrame(int i) {
        return frames.get(i);
    }
	
    /**
    * An inner class used to help manage timing of each frame in the animation.
    */
	class AnimFrame implements Serializable{
		transient BufferedImage image;
		long endTime;		// the totalDuration up to the end of this frame
							// is the endTime for this frame
		
		public AnimFrame(BufferedImage image, long endTime){
			this.image = image;
			this.endTime = endTime;
		}
		
		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
			ImageIO.write(this.image, "png", out);
		}
		
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();
			this.image = ImageIO.read(in);
		}
	}
}