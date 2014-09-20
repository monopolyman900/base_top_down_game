package cardMenu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Menu extends JPanel{

	boolean menuset;
	
	public Menu(){
		
		setBackground(Color.BLACK);
		
		JButton button = new JButton(new AbstractAction("hello"){
			@Override
			public void actionPerformed(ActionEvent e){
				menuset = false;
			}
		});	
		this.add(button);
	}
	
	public JPanel getPanel(){
		return this;
	}
	
	public void setMenuset(boolean x){
		menuset = x;
	}
	
	public boolean getMenuset(){
		return menuset;
	}
}
