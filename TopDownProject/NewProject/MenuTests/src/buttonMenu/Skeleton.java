package buttonMenu;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Skeleton extends JFrame implements ActionListener{

	JPanel menu;
	JButton button;
	
	public Skeleton(){
		
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setSize(400, 400);
	setVisible(true);
	
	menu = new JPanel();
	button = new JButton("hello");

	menu.setSize(400, 400);
	menu.setBackground(Color.BLACK);
	menu.setVisible(true);
	menu.add(button);
	
	button.setLocation(200, 200);
	button.addActionListener(this);
	
	add(menu, BorderLayout.CENTER);
	
	}
	
	public void actionPerformed(ActionEvent a){
	    JPanel panel = Game.Game();
	    this.remove(menu);
	    this.add(panel);   
	    revalidate();
		System.out.println("zzzzzzzzz");
	}
	
	public static void main(String args[]){
		new Skeleton();
	}
}
