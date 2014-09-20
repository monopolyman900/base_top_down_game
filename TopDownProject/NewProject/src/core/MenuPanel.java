package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import core.GamePanel.GameListener;

public class MenuPanel extends JPanel implements Runnable{

	boolean menuPanelDisplayed = true;
	
	private String[] menuItems;
	private int[] menuXOffset;
	private int selectedMenuItem;
	private int centerX;
	private Font font;
	
	private Thread thread;
	private int period = 20;
	private boolean running;
	
	private GameFrame frame;
	
	private boolean isUpHeld, isDownHeld;
	
	int panelWidth, panelHeight;
	Color bgColor, selectedColor, unselectedColor;
	Graphics g;
	
	public MenuPanel(int w, int h, GameFrame frame){
		this.panelWidth = w;
		this.panelHeight = h;
		this.frame = frame;
		this.bgColor = Color.BLACK;
		this.selectedColor = Color.WHITE;
		this.unselectedColor = Color.GRAY;

		setSize(panelWidth, panelHeight);
		setBackground(bgColor);
		
		this.addKeyListener(new MenuListener());
		this.setFocusable(true);
		
		thread = new Thread(this, "menuAnimator");
		
		centerX = w/2;
		font = new Font("Courier New", Font.PLAIN, 50);
		menuItems = new String[3];
		menuXOffset = new int[3];
		selectedMenuItem = 0;
		menuItems[0] = "Game";
		menuItems[1] = "Admin";
		menuItems[2] = "Close";
		menuXOffset[0] = 70;
		menuXOffset[1] = 83;
		menuXOffset[2] = 84;
		
		thread.start();
	}
	
	public void setPanelDisplayed(boolean x){
		this.menuPanelDisplayed = x;
	}
		
	public void paint(Graphics g){
		super.paint(g);
		g.setFont(font);
		for(int i = 0; i < menuItems.length; i++){
			if(i == selectedMenuItem){
				g.setColor(selectedColor);
			}
			else g.setColor(unselectedColor);
			g.drawString(menuItems[i], centerX-menuXOffset[i], 100+i*200);
		}
		
		//this helps with centering the letters
	/*	for(int i = 0; i < panelWidth; i++){
			int x = i*20;
			g.drawLine(x, 0, x, panelHeight);
		}	*/
	}

	public void run() {
		if(menuPanelDisplayed){
			running = true;
			while(running) {	//this is always running FIX THIS
				repaint();
				try {
					Thread.sleep(period);
				} catch(InterruptedException ex){}
			}
			System.exit(0);
		}
	}
	
	class MenuListener extends KeyAdapter {
		
	    public void keyPressed(KeyEvent e) {
	    	int key = e.getKeyCode();
	    	if(key == KeyEvent.VK_DOWN && !isUpHeld){
	    		if(selectedMenuItem < menuItems.length-1){
	    			selectedMenuItem++;
	    		}
	    		isDownHeld = true;
	    	}
	    	if(key == KeyEvent.VK_UP && !isDownHeld){
	    		if(selectedMenuItem > 0){
	    			selectedMenuItem--;
	    		}
	    		isUpHeld = true;
	    	}
	    	//selection
	    	if(key == KeyEvent.VK_ENTER && !isUpHeld && !isDownHeld){
	    		//game
	    		if(selectedMenuItem == 0){
	    			menuPanelDisplayed = false;
	    			frame.setPanelDisplayed("game");
	    		}
	    		//admin
	    		if(selectedMenuItem == 1){
	    			menuPanelDisplayed = false;
	    			frame.setPanelDisplayed("admin");
	    		}
	    		//close
	    		if(selectedMenuItem == 2){
	    			System.exit(0);
	    		}
	    	}
	    }
	    
	    public void keyReleased(KeyEvent e){
	    	int key = e.getKeyCode();
	    	if(key == KeyEvent.VK_DOWN){
	    		isDownHeld = false;
	    	}
	    	if(key == KeyEvent.VK_UP){
	    		isUpHeld = false;
	    	}
	    }
	}
}
