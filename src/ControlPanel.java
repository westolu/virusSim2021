import javax.swing.*;
import java.util.Scanner;
import java.awt.event.*;

public class ControlPanel extends JFrame {
    JFrame f;
    int delay = 10;
    public ControlPanel() {
        setTitle("Control panel");
        setSize(200, 75);
        // Create JButton and JPanel
        JButton buttonSlow = new JButton("<<");

        buttonSlow.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //your actions
                    delay = delay * 2;
                    //System.out.println(delay);
                    //if (delay<0){delay = 1;};
                }
            });
        //System.out.println(delay);
        JButton buttonReset = new JButton("||");
        buttonReset.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //your actions
                    delay = 10;
                }
            });
        JButton buttonFast = new JButton(">>");
        buttonFast.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //your actions
                    delay = delay / 2;
                    //System.out.println(delay);
                    //if (delay>100){delay = 1000;};
                }
            });
        JPanel panel = new JPanel();

        // Add button to JPanel
        panel.add(buttonSlow);
        panel.add(buttonReset);
        panel.add(buttonFast);
        // And JPanel needs to be added to the JFrame itself!
        this.getContentPane().add(panel);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        ControlPanel a = new ControlPanel();
    }
}