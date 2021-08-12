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
    
    public Display(int width, int height){
        this.width = width;
        this.height = height;
        
        createDisplay();
    }
    private void createDisplay(){
        setSize(400, 400);
        
        setTitle("virus simulation");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);
        
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        panel.add(button);
        this.getContentPane().add(panel);
        add(canvas);
        pack();
        this.toFront();
        setVisible(true);
    }
    
    public Canvas getCanvas(){
        return canvas;
    }
}
