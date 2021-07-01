
/**
 * Write a description of class displaycanvas here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.*;

public class Display extends JFrame {
    
    private JFrame frame;
    private Canvas canvas;
    private JPanel Panel1 = new JPanel();
    private JButton button = new JButton("speed up");
    
    
    private String title;
    private int width, height;
    
    public Display(String title, int width, int height){
        this.title = title;
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
        Panel1.add(button);
        this.getContentPane().add(Panel1);
        add(canvas);
        pack();
        this.toFront();
        setVisible(true);
    }
    
    public Canvas getCanvas(){
        return canvas;
    }
}
