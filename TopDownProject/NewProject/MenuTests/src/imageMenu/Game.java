package imageMenu;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Game{	
	
	JFrame skeleton;
	JPanel game1;
	
	public Game(){
		
		skeleton = new JFrame();
		game1 = new JPanel();
		
		ImageIcon ii = new ImageIcon(this.getClass().getResource("craft.png"));
		JLabel label = new JLabel(ii);
		
		skeleton.setVisible(true);
		skeleton.setSize(400, 400);
		
		game1.setSize(400, 400);
		game1.setBackground(Color.BLACK);
		
		skeleton.add(game1);	
		game1.add(label);
		
		//IMPORTANT--must be 'mouseClicked' to work--
				label.addMouseListener(new MouseAdapter(){
					@Override
					public void mouseClicked(MouseEvent e){
						Game2 game2 = new Game2();
						skeleton.remove(game1);
						skeleton.add(game2.getGame2());
						skeleton.revalidate();
					}
					
				});
	}
	
	public JPanel getGame1(){
		return game1;
	}
	
	public JFrame getSkeleton(){
		return skeleton;
	}
}
	

