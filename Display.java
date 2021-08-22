import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.*;

public class Display extends JFrame {
    private Canvas canvas;
    private final JPanel panel = new JPanel();
    private final JButton button = new JButton("speed up");


    private final int width;
    private final int height;
    
    public Display(int width, int height){  //take in two inputs, width and height
        this.width = width;
        this.height = height;
        
        createDisplay();
    }
    private void createDisplay(){
        setSize(400, 400);          //initially set window to be 400*400
        
        setTitle("virus simulation");
        setSize(width, height); //resize to users input
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //make the close operation to, when the user closes the window, to exit.
        setResizable(true);
        setLocationRelativeTo(null);
        
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));  //set canvases max, min, and prefered size.
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        panel.add(button);
        this.getContentPane().add(panel);
        add(canvas);
        pack();
        this.toFront(); //send the frame to the front of the windows, so the user doesnt have to alt-tab.
        setVisible(true);
    }
    
    public Canvas getCanvas(){
        return canvas;
    }
}
