import java.io.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.io.File;
import java.io.FileWriter;
import javax.swing.*;
import java.applet.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.concurrent.*;

/**
 * Write a description of class Grapher here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Grapher implements Runnable{
    private Display display;
    public int width, height;
    public String title;

    private boolean running = false;
    private Thread thread;
    long TimeStep;

    private BufferStrategy bs;
    private Graphics g;

    long delay = 10;
    int minVel = 1;
    int maxVel = 100;
    int ovalWidth = 50;
    int ovalHeight = 50;
    int eyeWidth = ovalWidth/5;
    int eyeHeight = (ovalHeight*2)/5;

    private int xPos[];
    private int xVel[];
    private int yPos[];
    private int yVel[];

    Scanner input = new Scanner(System.in);
    Preferences prefs = new Preferences();

    boolean read = false;
    public Grapher(String title, int width, int height){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    private void initialize() throws Exception{
        int peopleInfected = 0;
        System.out.println("note: infected patients cannot be reinfected, and so cannot reset their timer");
        System.out.println("note: enter -1 for default value, width/height cannot be less than 3.");
        System.out.println("presets are: population size of 50, world width of 10, world height of 10, run for 10 days, 10 people to start infected,");
        System.out.println("people are infected for 10 days before being cured, and are immune for 3 days after being cured, repeat simulation 20 times.");
        String[] prompts = new String[] {"enter population", "enter width of world", "enter height of world", "enter days to run", "enter number of people to start as infected", 
        "enter how long people are infected for", "enter how long people are immune for after they are cured", "enter how many times to repeat"};
        File file = new File ("output.txt");
        
        for(int z=0; z<8; z++){
        System.out.println(prompts[z]);
        int temp = input.nextInt();
        if(temp != -1){
        prefs.vars[z] = temp;
        }
        }
        
        // String[][] world = new String[prefs.vars[1]][prefs.vars[2]];
        // FileWriter writer = new FileWriter(file);
        display = new Display(title, prefs.vars[1], prefs.vars[2]);
        ControlPanel control = new ControlPanel();
        
        xPos = new int[prefs.vars[0]];
        yPos = new int[prefs.vars[0]];
        xVel = new int[prefs.vars[0]];
        yVel = new int[prefs.vars[0]];
        
        for(int i = 0; i<prefs.vars[0]; i++){
        xPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[1]);
        yPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[2]);
        xVel[i] = ThreadLocalRandom.current().nextInt(minVel, maxVel);
        yVel[i] = ThreadLocalRandom.current().nextInt(minVel, maxVel);
    }
    }

    public void tickRun(){
        try {
            for(int i = 0; i < prefs.vars[0]; i++){
                xPos[i] = xPos[i] + xVel[i];
                yPos[i] = yPos[i] + yVel[i];
                if(xPos[i] >= width - 50){
                    xVel[i] = xVel[i] * -1;
                }
                if(yPos[i] >= height - 50){
                    yVel[i] = yVel[i] * -1;
                }
                if(xPos[i] <= 0){
                    xVel[i] = xVel[i] * -1;
                }
                if(yPos[i] <= 0){
                    yVel[i] = yVel[i] * -1;
                }
                //Thread.sleep(1);
                for(int j = 0; j<prefs.vars[0]; j++){
                //if(xPos[i] +  == ){
                    
                //}
            }
            }

            render();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void render(){
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        //clear screen
        g.clearRect(0, 0, prefs.vars[1], prefs.vars[2]);
        
        //draw people
        for(int i = 0; i < prefs.vars[0]; i++){
            g.setColor(Color.green);
            g.fillOval(xPos[i], yPos[i], ovalWidth, ovalHeight);
            g.setColor(Color.black);
            
            g.fillOval(xPos[i]+10, yPos[i]+10, eyeWidth, eyeHeight);
            g.fillOval(xPos[i]+32, yPos[i]+10, eyeWidth, eyeHeight);
            g.drawArc(xPos[i]+10, yPos[i]+15, 32, 30, 180, 180);
        }
        

        //draw ellipse
        bs.show();
        g.dispose();
    }

    public void run(){

        while(running){
            tickRun();

            //render();
        }

        stop();
    }

    public synchronized void start(){
        try {
            initialize();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        if(running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop(){
        if(!running)
            return;
        running = false;
        try{
            thread.join();
        }
        catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }
}
