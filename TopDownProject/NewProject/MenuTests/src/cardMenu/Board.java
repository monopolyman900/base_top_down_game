package cardMenu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Board extends JPanel{
	
//for some reason boardset is always true-- in case of timer methods	
	
	boolean boardset;
	
	public Board(){
	setBackground(Color.WHITE);
	
	JButton button = new JButton(new AbstractAction("hello2"){
		@Override
		public void actionPerformed(ActionEvent e){
			boardset = false;
		}
	});	
	this.add(button);
}
	
	public JPanel getPanel(){
		return this;
	}
	
	public void setBoardset(boolean x){
		boardset = x;
	}
	
	public boolean getBoardset(){
		return boardset;
	}
}
