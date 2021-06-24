import java.io.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.io.File;
import javax.swing.*;
import java.applet.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.FileWriter;
import java.util.concurrent.*;

/**
 * Write a description of class Grapher here.
 *
 * @author (Luke Weston)
 * @version (23.0)
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

    private final int MIN_VEL = -3;     //HERE i make all my arrays/constants
    private final int MAX_VEL = 3;
    private final int OVAL_DIAM = 50;
    private final int EYE_WIDTH = OVAL_DIAM/5;
    private final int EYE_HEIGHT = (OVAL_DIAM*2)/5;
    int smileAngle;
    int smileXPos;
    int smileYPos;
    boolean inputCheck;

    private int xPos[];         //HERE i make all my arrays/constants
    private int xVel[];
    private int yPos[];
    private int yVel[];
    private boolean infected[];
    private long infectedTime[];
    private boolean immune[]; // this variable is just for coloring the people if they are immune
    private double dx;
    private double dy;
    private double distance;
    private final long FRAME_TIME = 10000000; //this is in nanoseconds

    Scanner input = new Scanner(System.in);
    Preferences prefs = new Preferences();

    boolean read = false;
    public Grapher(String title, int width, int height){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    private void initialize(){
        int peopleInfected = 0;
        System.out.println("note: infected patients cannot be reinfected, and so cannot reset their timer");
        System.out.println("note: enter -1 for default value, width/height cannot be less than 3.");
        System.out.println("presets are: population size of 50, world width of 10, world height of 10, run for 10 days, 10 people to start infected,");
        System.out.println("people are infected for 10 days before being cured, and are immune for 3 days after being cured, repeat simulation 20 times.");
        String[] prompts = new String[] {"enter population", "enter width of world", "enter height of world", "enter days to run", "enter number of people to start as infected", 
                "enter how long people are infected for, in cycles", "enter how long people are immune for after they are cured, in cycles", "enter how many times to repeat"};
        File file = new File ("output.txt");
        System.out.println("enter true to use your own settings, anything else to use default");
        try{
            inputCheck = Boolean.parseBoolean(input.nextLine());
        }catch(Exception e){
        }
        if(inputCheck == true){
            for(int z=0; z<8; z++){
                System.out.println(prompts[z]);
                Integer temp = null;
                do{
                    try{
                        temp = Integer.parseInt(input.nextLine());
                    }catch(NumberFormatException e){
                        System.out.println("invalid input, try again :D");
                    }
                } while (temp == null);
                if(temp != -1){ //input checking
                    prefs.vars[z] = temp;
                }

            }
        }

        // String[][] world = new String[prefs.vars[1]][prefs.vars[2]];
        // FileWriter writer = new FileWriter(file);
        display = new Display(title, prefs.vars[1], prefs.vars[2]);
        //ControlPanel control = new ControlPanel();

        xPos = new int[prefs.vars[0]]; //create all my arrays to be full of people
        yPos = new int[prefs.vars[0]];
        xVel = new int[prefs.vars[0]];
        yVel = new int[prefs.vars[0]];
        infected = new boolean[prefs.vars[0]];
        infectedTime = new long[prefs.vars[0]];
        immune = new boolean[prefs.vars[0]];

        for(int i = 0; i<prefs.vars[0]; i++){
            xPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[1] - OVAL_DIAM); //randomise positions and velocities between bounds
            yPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[2] - OVAL_DIAM);
            xVel[i] = ThreadLocalRandom.current().nextInt(MIN_VEL, MAX_VEL);
            yVel[i] = ThreadLocalRandom.current().nextInt(MIN_VEL, MAX_VEL);
            infected[i] = false;
            infectedTime[i] = 0;
            immune[i] = false;
            for(int q = 0; q < prefs.vars[0]; q++){
                for(int y = 0; y < prefs.vars[0]; y++){    
                    dx = (xPos[q] + OVAL_DIAM/2) - (xPos[y] + OVAL_DIAM/2); //find difference in x
                    dy = (yPos[q] + OVAL_DIAM/2) - (yPos[y] + OVAL_DIAM/2); //find difference in y
                    distance = Math.sqrt(dx * dx + dy * dy); //find distance between using pythag
                    if(distance <= OVAL_DIAM){
                        xPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[1] - OVAL_DIAM); //randomise positions and velocities between bounds
                        yPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[2] - OVAL_DIAM);
                    }else{
                    }
                }
            }
        }
        for(int j = 0; j < prefs.vars[4]; j++){
            infected[j] = true;
            infectedTime[j] = prefs.vars[5];
        }
    }

    public void tickRun(){
        long startTime = System.nanoTime();
        try {
            for(int i = 0; i < prefs.vars[0]; i++){
                if(xPos[i] >= prefs.vars[1] - OVAL_DIAM){
                    xVel[i] = xVel[i] * -1;
                    xPos[i] = prefs.vars[1] - OVAL_DIAM;
                }
                if(yPos[i] >= prefs.vars[2] - OVAL_DIAM){
                    yVel[i] = yVel[i] * -1;
                    yPos[i] = prefs.vars[2] - OVAL_DIAM;
                }
                if(xPos[i] <= 0){
                    xVel[i] = xVel[i] * -1;
                    xPos[i] = 5;
                }
                if(yPos[i] <= 0){
                    yVel[i] = yVel[i] * -1;
                    yPos[i] = 5;
                }
                for(int j = 0; j<prefs.vars[0]; j++){ //hit detection
                    if(i != j){
                        dx = (xPos[i] + OVAL_DIAM/2) - (xPos[j] + OVAL_DIAM/2); //find difference in x
                        dy = (yPos[i] + OVAL_DIAM/2) - (yPos[j] + OVAL_DIAM/2); //find difference in y
                        distance = Math.sqrt(dx * dx + dy * dy) + 5; //find distance between using pythag
                        if(immune[i] == false && immune[j] == false && infected[i] || infected[j]){
                            if(distance < OVAL_DIAM){
                                infected[i] = true;
                                infected[j] = true;
                                int tempXVel  = xVel[i];
                                int tempYVel  = yVel[i];
                                xVel[i] = xVel[j];
                                yVel[i] = yVel[j];
                                xVel[j] = tempXVel;
                                yVel[j] = tempYVel;
                                if(xPos[i] < xPos[j]){
                                    xPos[i] = xPos[i] - 1;
                                }else{
                                    xPos[j] = xPos[j] - 1;
                                }
                                if(yPos[i] < yPos[j]){
                                    yPos[i] = yPos[i] - 1;
                                }else{
                                    yPos[j] = yPos[j] - 1;
                                }
                                infectedTime[i] = prefs.vars[5];
                                infectedTime[j] = prefs.vars[5];
                            }else{
                            }
                        }else{
                            if(distance < OVAL_DIAM){
                                int tempXVel  = xVel[i];
                                int tempYVel  = yVel[i];
                                xVel[i] = xVel[j];
                                yVel[i] = yVel[j];
                                xVel[j] = tempXVel;
                                yVel[j] = tempYVel;
                                if(xPos[i] < xPos[j]){
                                    xPos[i] = xPos[i] - 5;
                                }else{
                                    xPos[j] = xPos[j] - 5;
                                }
                            }else{
                            }
                        }
                    }
                }
                xPos[i] = xPos[i] + xVel[i];
                yPos[i] = yPos[i] + yVel[i];
            }
            for(int q=0; q<prefs.vars[0]; q++){
                if(infectedTime[q] == 1){
                    infected[q] = false;
                    infectedTime[q] = -prefs.vars[6];
                    immune[q] = true;
                }else if(infectedTime[q] > 0){
                    infectedTime[q]--;
                }else if(infectedTime[q] == -1){
                    infectedTime[q] = 0;
                    immune[q] = false;
                }else if(infectedTime[q] < -1){
                    infectedTime[q]++;
                }
            }
            long endTime = System.nanoTime();
            long processTime = endTime - startTime;
            long timeToWait = FRAME_TIME - processTime;
            if(timeToWait <= 0){
                System.out.println("uh oh something has gone wrong, the simulation is taking too long");
                timeToWait = 0;
            }
            timeToWait = timeToWait/1000000; //convert to milliseconds
            Thread.sleep(timeToWait);
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
        g.clearRect(0, 0, 2500, 2500);

        //draw people
        for(int i = 0; i < prefs.vars[0]; i++){
            g.setColor(Color.black);
            g.fillOval(xPos[i], yPos[i], OVAL_DIAM, OVAL_DIAM);
            if(infected[i] == true){
                g.setColor(Color.red);
                smileAngle = 0;
                smileYPos = yPos[i]+30;
            }else if(immune[i] == true){ 
                g.setColor(Color.blue);
                smileAngle = 180;
                smileYPos = yPos[i]+10;
            }else{
                g.setColor(Color.green);
                smileAngle = 180;
                smileYPos = yPos[i]+10;
            }
            g.fillOval(xPos[i] + (OVAL_DIAM*1/20), yPos[i] + ((OVAL_DIAM*1/10)/2), OVAL_DIAM*9/10, OVAL_DIAM*9/10);
            //g.setStroke(new BasicStroke(1));
            g.setColor(Color.black);
            g.fillOval(xPos[i]+(OVAL_DIAM/4), yPos[i]+(OVAL_DIAM/5), EYE_WIDTH, EYE_HEIGHT);
            g.fillOval(xPos[i]+((OVAL_DIAM*3)/5), yPos[i]+(OVAL_DIAM/5), EYE_WIDTH, EYE_HEIGHT);
            g.drawArc(xPos[i]+(OVAL_DIAM/5), smileYPos, ((OVAL_DIAM*6)/10), ((OVAL_DIAM*6)/10), smileAngle, 180);
        }
        bs.show();
        g.dispose();
    }

    public void run(){
        try{
            render();
            Thread.sleep(1000);
        }catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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
