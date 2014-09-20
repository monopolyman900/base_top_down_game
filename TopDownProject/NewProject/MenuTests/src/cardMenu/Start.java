package cardMenu;

import javax.swing.JFrame;

public class Start extends JFrame{

	public Start(){
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 400);
		setVisible(false);
	
		new Skeleton(this);
		
	}
	
	
	public static void main(String args[]){
		new Start();
	}
}
