package animation;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import animation.Sprite;
//This is done
public class SpriteListener extends KeyAdapter {
	
	private Sprite sprite;
	
	public SpriteListener(Sprite sprite) {
		this.sprite = sprite;
	}
	
	// do while key is released
    public void keyReleased(KeyEvent e) {
        sprite.keyReleased(e);
    }

    // do while key is pressed down
    public void keyPressed(KeyEvent e) {
        sprite.keyPressed(e);
    } 
    
    public void keyTyped(KeyEvent e) {
        sprite.keyTyped(e);
    } 

}
