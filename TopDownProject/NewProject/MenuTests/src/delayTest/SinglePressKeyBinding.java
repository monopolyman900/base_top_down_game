package delayTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SinglePressKeyBinding {

    public static void main(String[] args) {
        new SinglePressKeyBinding();
    }

    public SinglePressKeyBinding() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private JLabel message;

        private boolean spacedOut = false;

        public TestPane() {
            message = new JLabel("Waiting");
            setLayout(new GridBagLayout());
            add(message);

            InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space-pressed");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "space-released");

            am.put("space-pressed", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (spacedOut) {
                        message.setText("I'm ignoring you");
                    } else {
                        spacedOut = true;
                        message.setText("Spaced out");
                    }
                }
            });
            am.put("space-released", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    spacedOut = false;
                    message.setText("Back to earth");
                }
            });

        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

    }
}