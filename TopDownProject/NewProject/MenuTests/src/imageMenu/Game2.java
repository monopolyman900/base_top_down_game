package imageMenu;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Game2{

	JPanel game2;	
	Game game1;
	
	public Game2(){
		
	game2 = new JPanel();
	game2.setBackground(Color.WHITE);
	game2.setSize(400, 400);
	
	ImageIcon ii = new ImageIcon(this.getClass().getResource("craft.png"));
	JLabel label = new JLabel(ii);
	
	label.addMouseListener(new MouseAdapter(){
		
		@Override
		public void mouseClicked(MouseEvent e) {
			//starts over in new window
			game1 = new Game();
			
			game1.getSkeleton().remove(game2);
			game1.getSkeleton().add(game1.getGame1());
			game1.getSkeleton().revalidate();
		}
		
	});
	
	game2.add(label);
	}
	
	public JPanel getGame2(){
		return game2;
	}
}
