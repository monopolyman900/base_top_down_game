package animation;

public class CollidableObject extends Sprite{

	//This class sets/gets if an object is collidable & sets/gets if it is onscreen.
	
	private boolean isCollidable;
	private boolean inpenetrable;
	private boolean isOnScreen;
	
	public CollidableObject(int pixelX, int pixelY, String ID){
		super(pixelX, pixelY, ID);
		this.isCollidable = true;
		setIsOnScreen(false);
	}
	
	public CollidableObject(){
		super(0, 0, null);
		this.isCollidable = true;
		setIsOnScreen(false);
	}

	public boolean isCollidable() {
		return isCollidable;
	}
	
	public void setIsCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}
	
	public boolean inpenetrable(){
		return inpenetrable;
	}
	
	public void setIsInpenetrable(boolean inpenetrable){
		this.inpenetrable = inpenetrable;
	}
	
	public boolean isOnScreen() {
		return isOnScreen;
	}
	
	public void setIsOnScreen(boolean isOnScreen) {
		this.isOnScreen = isOnScreen;
	}
}