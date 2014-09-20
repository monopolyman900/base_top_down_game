package buttonMenu;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Game{

	Skeleton skeleton;
	static JButton button;
	
	public static JPanel Game(){
		
		JPanel panel = new JPanel();
		panel.setSize(400, 400);
		panel.setBackground(Color.WHITE);
		
		return panel;		
	}
	
	
	
}
