import java.awt.*;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.util.concurrent.*;

/**
 * Write a description of class Grapher here.
 *
 * @author (Luke Weston)
 * @version (27.0)
 */
public class Grapher implements Runnable{
    private Display display;

    private boolean running = false;
    private Thread thread;
    private Charter theChart;

    private static final int MIN_VEL = -3;     //HERE i make all my arrays/constants
    private static final int MAX_VEL = 3;
    private static final int OVAL_DIAM = 40;   //diameter of the people
    private static final int EYE_WIDTH = OVAL_DIAM/5; //eye width/height is a factor of the oval diameter, so that no matter how wide the ovals are, the eyes stay in proportion
    private static final int EYE_HEIGHT = (OVAL_DIAM*2)/5;


    private boolean printPrefs;

    private int totalPeopleInfected = 0;
    private int totalCycles = 0;
    private int totalPeopleCured = 0;
    private int xPos[];         
    private int xVel[];         
    private int yPos[];
    private int yVel[];
    private boolean infected[];         //boolean for if the person is infected or not
    private long infectedTime[];        //how long the people are infected/immune people are, when they are infected (red), it is set to a +ve number that ticks down, and when the infected time 
    //reaches 1 then they are immune (blue) and the infected time is set to a -ve number and ticks up until it reaches 0, where the person it set to be healthy (green)
    private boolean immune[];           //this variable is just for coloring the people if they are immune
    private double dx;
    private double dy;
    private double distance;
    private static final long FRAME_TIME = 10000000; //this is in nanoseconds

    private Scanner input = new Scanner(System.in);
    private Preferences prefs = new Preferences();//initialise default preferences


    private Grapher(){

    }
    public static void main(String[] args) {
        Grapher grapher = new Grapher();
        grapher.start();
    }
    private void initialize(){
        boolean inputCheck;
        System.out.println("do you want to have each individual data point printed, for use in plotting with excel? input 'true' to have all data points printed.");
        printPrefs = Boolean.parseBoolean(input.nextLine());        //get a boolean from the user, if they user types anything but true the does not print each data point
        System.out.println("note: enter -1 for default value, width/height must be more than 300.");
        System.out.println("presets are: population size of 10, world width of 600, world height of 600, run for 3000 cycles, 1 person to start infected,");
        System.out.println("people are infected for 300 cycles before being cured, and are immune for 250 cycles after being cured");
        //create an array of all the text I will output when I am asking about the users input.
        String[] prompts = new String[] {"enter population", "enter width of world", "enter height of world", "enter how many cycles to run", 
                "enter number of people to start as infected", "enter how long people are infected for, in cycles", 
                "enter how long people are immune for after they are cured, in cycles",};
        System.out.println("enter true to use your own settings, anything else to use default");


        inputCheck = Boolean.parseBoolean(input.nextLine());        //get a boolean from the user, if they user types anything but true the simulation uses default settings

        if(inputCheck){         //if inputcheck is true, then ask the user for their settings they want to use
            for(int z=0; z<7; z++){
                System.out.println(prompts[z]);
                Integer temp = null;
                do{
                    try{
                        temp = Integer.parseInt(input.nextLine());
                    }catch(NumberFormatException e){
                        System.out.println("invalid input, try again :D");          //if they do not type a number, then ask them to put in a different number.
                    }
                } while (temp == null);
                if(temp > 0){ //input checking
                    if(z == 1 || z == 2){                       // if the input is for the width (1 in my array of settings) or height (2 in my array of settings)
                        if(temp >= 300){
                            prefs.vars[z] = temp;
                        }else{
                            System.out.println("sorry, width/height cannot be less than 300, please enter a value at or above 60.");
                            z--;                              //move the array back one, asking again
                        }
                    }else {
                        prefs.vars[z] = temp;
                    }
                }else{
                    System.out.println("Please enter a number above 0, or -1 to use default");
                    z--;
                }
            }
        }

        // String[][] world = new String[prefs.vars[1]][prefs.vars[2]];
        // FileWriter writer = new FileWriter(file);
        display = new Display(prefs.vars[1], prefs.vars[2]);
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
                    dx = (xPos[q] + OVAL_DIAM*0.5) - (xPos[y] + OVAL_DIAM*0.5); //find difference in x
                    dy = (yPos[q] + OVAL_DIAM*0.5) - (yPos[y] + OVAL_DIAM*0.5); //find difference in y
                    distance = Math.sqrt(dx * dx + dy * dy); //find distance between using pythag
                    if(distance <= OVAL_DIAM){      //if two people are overlapping
                        xPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[1] - OVAL_DIAM); //randomise positions and velocities between bounds
                        yPos[i] = ThreadLocalRandom.current().nextInt(1, prefs.vars[2] - OVAL_DIAM);
                    }
                }
            }
        }
        for(int j = 0; j < prefs.vars[4]; j++){
            infected[j] = true;
            infectedTime[j] = prefs.vars[5];
            totalPeopleInfected++;
        }
        theChart = new Charter("Real-Time XChart", "# of cycles", "# of people", "people infected", 0, 0);
    }

    private void tickRun(){
        if(totalCycles < prefs.vars[3]){
            long startTime = System.nanoTime();
            try {
                for(int i = 0; i < prefs.vars[0]; i++){
                    if(xPos[i] >= prefs.vars[1] - OVAL_DIAM){               //if person is at the edge of the screen
                        xVel[i] = xVel[i] * -1;                             //reverse velocity
                        xPos[i] = prefs.vars[1] - OVAL_DIAM;
                    }
                    if(yPos[i] >= prefs.vars[2] - OVAL_DIAM){               //if person is at the edge of the screen
                        yVel[i] = yVel[i] * -1;                             //reverse velocity
                        yPos[i] = prefs.vars[2] - OVAL_DIAM;
                    }
                    if(xPos[i] <= 0){                                       //if person is at the edge of the screen
                        xVel[i] = xVel[i] * -1;                             //reverse velocity
                        xPos[i] = 5;
                    }
                    if(yPos[i] <= 0){                                       //if person is at the edge of the screen
                        yVel[i] = yVel[i] * -1;                             //reverse velocity
                        yPos[i] = 5;
                    }
                    for(int j = 0; j<prefs.vars[0]; j++){ //hit detection
                        if(i != j){
                            dx = (xPos[i] + OVAL_DIAM*0.5) - (xPos[j] + OVAL_DIAM*0.5); //find difference in x
                            dy = (yPos[i] + OVAL_DIAM*0.5) - (yPos[j] + OVAL_DIAM*0.5); //find difference in y
                            distance = Math.sqrt(dx * dx + dy * dy) + 5; //find distance between using pythag
                            if(!immune[i] && !immune[j]){
                                if(infected[i]){
                                    if(distance < OVAL_DIAM){
                                        infected[j] = true;             //infect other person
                                        totalPeopleInfected++;
                                        int tempXVel  = xVel[i];        //swap velocities
                                        int tempYVel  = yVel[i];
                                        xVel[i] = xVel[j];
                                        yVel[i] = yVel[j];
                                        xVel[j] = tempXVel;
                                        yVel[j] = tempYVel;
                                        if(xPos[i] < xPos[j]){          //move apart, x direction
                                            xPos[i] = xPos[i] - 1;
                                        }else{
                                            xPos[j] = xPos[j] - 1;
                                        }
                                        if(yPos[i] < yPos[j]){          //move apart, y direction
                                            yPos[i] = yPos[i] - 1;
                                        }else{
                                            yPos[j] = yPos[j] - 1;
                                        }
                                        infectedTime[i] = prefs.vars[5];
                                        infectedTime[j] = prefs.vars[5];
                                    }
                                }
                            }else{
                                if(distance < OVAL_DIAM){
                                    int tempXVel  = xVel[i];
                                    int tempYVel  = yVel[i];        //same collision detection, but between immune people and infected people
                                    xVel[i] = xVel[j];
                                    yVel[i] = yVel[j];
                                    xVel[j] = tempXVel;
                                    yVel[j] = tempYVel;
                                    if(xPos[i] < xPos[j]){
                                        xPos[i] = xPos[i] - 5;
                                    }else{
                                        xPos[j] = xPos[j] - 5;
                                    }
                                }
                            }
                        }
                    }
                    xPos[i] = xPos[i] + xVel[i];        //move one tick of movement to prevent overlaps
                    yPos[i] = yPos[i] + yVel[i];
                }
                for(int q=0; q<prefs.vars[0]; q++){
                    if(infectedTime[q] == 1){
                        infected[q] = false;                    //this block of code ticks up/down the timers for infectiousness and immunity.
                        infectedTime[q] = -prefs.vars[6];
                        immune[q] = true;
                        totalPeopleCured++;
                    }else if(infectedTime[q] > 0){
                        infectedTime[q]--;
                    }else if(infectedTime[q] == -1){
                        infectedTime[q] = 0;
                        immune[q] = false;
                    }else if(infectedTime[q] < -1){
                        infectedTime[q]++;
                    }
                }
                if(printPrefs){
                    System.out.println(totalCycles + " " + totalPeopleInfected + " " + totalPeopleCured);
                }
                theChart.addNewData(totalCycles, totalPeopleInfected);
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
            totalCycles++;
        }else{
            System.out.println("simulation ended after " + totalCycles + " cycles, " + totalPeopleInfected + " people were infected and " + totalPeopleCured + " people were cured.");
            stop();
        }
    }

    private void render(){
        int smileAngle;
        int smileYPos;
        int smileWidth;
        int smileHeight;
        BufferStrategy bs;
        Graphics g;
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
            if(infected[i]){
                g.setColor(Color.red);
                smileAngle = 0;
                smileYPos = yPos[i]+25;
                smileWidth = ((OVAL_DIAM*6)/10);
                smileHeight = (OVAL_DIAM*4)/12;
            }else if(immune[i]){
                g.setColor(Color.blue);
                smileAngle = 180;
                smileYPos = yPos[i]+10;
                smileWidth = ((OVAL_DIAM*6)/10);
                smileHeight = (OVAL_DIAM*6)/10;
            }else{
                g.setColor(Color.green);
                smileAngle = 180;
                smileYPos = yPos[i]+10;
                smileWidth = ((OVAL_DIAM*6)/10);
                smileHeight = (OVAL_DIAM*6)/10;
            }
            g.fillOval(xPos[i] + (OVAL_DIAM/20), yPos[i] + ((OVAL_DIAM/10)/2), OVAL_DIAM*9/10, OVAL_DIAM*9/10);
            //g.setStroke(new BasicStroke(1));
            g.setColor(Color.black);
            g.fillOval(xPos[i]+(OVAL_DIAM/4), yPos[i]+(OVAL_DIAM/5), EYE_WIDTH, EYE_HEIGHT);
            g.fillOval(xPos[i]+((OVAL_DIAM*3)/5), yPos[i]+(OVAL_DIAM/5), EYE_WIDTH, EYE_HEIGHT);
            g.drawArc(xPos[i]+(OVAL_DIAM/5), smileYPos, smileWidth, smileHeight, smileAngle, 180);
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
        }

        stop(); //i want you to comment them with what they do because you dont know!!! (pleading)
    }

    private synchronized void start(){
        try {//accounting for errors OMG
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

    private synchronized void stop(){
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
