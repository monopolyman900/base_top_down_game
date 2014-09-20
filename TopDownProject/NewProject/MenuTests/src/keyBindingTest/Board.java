package keyBindingTest;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Board {
	
	ButtonListener buttonlistener;	
	EnterAction enterAction;
	
	public Board(){
		
		JFrame skeleton = new JFrame();
		skeleton.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		skeleton.setVisible(true);
		skeleton.setSize(400, 400);
		
		buttonlistener = new ButtonListener();
		enterAction = new EnterAction();
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		
		JButton button = new JButton("button");
		button.addActionListener(buttonlistener);
		panel.add(button);
		skeleton.add(panel);	

//panel is the JPanel, .put(define key, followed by String doEnterAction, used to pair to AbstractAction		
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "doEnterAction");
//pairs String pair from input map with abstract action
		panel.getActionMap().put("doEnterAction", enterAction);
		
	}
	
	public class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {		
			System.out.println("button pressed");
		}		
	}
	
//action for pressed key. Linked with enterAction defined above.
	public class EnterAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("enter pressed");	
		}		
	}
	
	public static void main(String[] args){
		new Board();
	}
}
