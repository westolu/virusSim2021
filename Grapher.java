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
    private int currentPeopleInfected = 0;
    private int currentPeopleImmune = 0;
    private int xPos[];                             //each bit of these arrays corresponds to a person, and their position/velocity. This is so
    private int xVel[];                             //I can create as many I want by changing the population
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
        System.out.println("note: green = healthy, blue = immune, red = infected.");
        System.out.println("do you want to have each individual data point printed, for use in plotting with excel? input 'true' to have all data points printed.");
        printPrefs = Boolean.parseBoolean(input.nextLine());        //get a boolean from the user, if they user types anything but true the does not print each data point
        System.out.println("note: enter -1 for default value, width/height must be more than 600.");
        System.out.println("presets are: population size of 10, world width of 600, world height of 600, run for 3000 cycles, 1 person to start infected,");
        System.out.println("people are infected for 300 cycles before being cured, and are immune for 250 cycles after being cured");
        System.out.println("default infection chance is 100%");
        //create an array of all the text I will output when I am asking about the users input.
        String[] prompts = new String[] {"enter population", "enter width of world", "enter height of world", "enter how many cycles to run", 
                "enter number of people to start as infected", "enter how long people are infected for, in cycles", 
                "enter how long people are immune for after they are cured, in cycles", "enter the infection chance, a number between 0 and 100"};
        System.out.println("enter true to use your own settings, anything else to use default");


        inputCheck = Boolean.parseBoolean(input.nextLine());        //get a boolean from the user, if they user types anything but true the simulation uses default settings

        if(inputCheck){         //if inputcheck is true, then ask the user for their settings they want to use
            for(int z=0; z<8; z++){
                System.out.println(prompts[z]);
                Integer temp = null;
                do{
                    try{
                        temp = Integer.parseInt(input.nextLine());                  //set temp to be the users input for input checking purposes
                    }catch(NumberFormatException e){
                        System.out.println("invalid input, try again :D");          //if they do not type a number, then ask them to put in a different number.
                    }
                } while (temp == null);
                if(temp > 0){ //input checking
                    if(z == 1 || z == 2){                       // if the input is for the width (1 in my array of settings) or height (2 in my array of settings)
                        if(temp >= 600){                        // and it is inside the bounds, set the preferences value corresponding to the width/height to the temporary value.
                            prefs.vars[z] = temp;
                        }else{
                            System.out.println("sorry, width/height cannot be less than 600, please enter a value at or above 600.");
                            z--;                              //move the array back one, asking again
                        }
                    }else {
                        prefs.vars[z] = temp;
                    }
                    if(z==8){
                        if(temp<=100 && temp>=0){       //same method, if the infection chance is not in the wanted range then we ask again for the value of the infection chance.
                            System.out.println("sorry, infection chance must be more than  or equal to 0 and must be less than or equal to 100, please enter a value accordingly.");
                            z--;                              //move the array back one, asking again
                        }
                    }
                }else{
                    System.out.println("Please enter a number above 0, or -1 to use default"); //if the input is not above 0, or is not equal to -1, then ask the user to enter the 
                    z--;                                                                       //number again.
                }
            }
        }

        // String[][] world = new String[prefs.vars[1]][prefs.vars[2]];
        // FileWriter writer = new FileWriter(file);
        display = new Display(prefs.vars[1], prefs.vars[2]);
        //ControlPanel control = new ControlPanel();

        xPos = new int[prefs.vars[0]]; //create all my arrays to be full of people using the preferences value for the desired population.
        yPos = new int[prefs.vars[0]]; //prefs.vars[0] is the population setting
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
            infected[i] = false;//set everyone to initially be healthy and not immune.
            infectedTime[i] = 0;
            immune[i] = false;
            for(int q = 0; q < prefs.vars[0]; q++){         //this code is to make sure no one is initially overlapping with each other by checking if the distance between them is 
                for(int y = 0; y < prefs.vars[0]; y++){     //less than the diameter of the people.
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
        for(int j = 0; j < prefs.vars[4]; j++){         //initially infect people until there are as many people infected as the user wants.
            infected[j] = true;
            infectedTime[j] = prefs.vars[5];
            totalPeopleInfected++;
            currentPeopleInfected++;
        }
        theChart = new Charter("Infection chart", "# of cycles", "# of people", "people infected", 0, 0); //create the chart with the appropriate axis titles
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
                                        if(ThreadLocalRandom.current().nextInt(0, 100) < prefs.vars[7]) { //% chance for the person to get infected, this is given by the user, 100% by default.
                                            if (!infected[j]) {
                                                totalPeopleInfected++;
                                                currentPeopleInfected++;
                                                infected[j] = true;             //infect other person
                                            }
                                        }
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

                                }
                            }
                        }
                    }
                    xPos[i] = xPos[i] + xVel[i];        //move one tick of movement to prevent overlaps
                    yPos[i] = yPos[i] + yVel[i];
                }
                for(int q=0; q<prefs.vars[0]; q++){      //this block of code ticks up/down the timers for infectiousness and immunity.
                    if(infectedTime[q] == 1){            //at the end of the infection duration, make the person immune and also set their infected timer to a -ve value, signalling for it to tick up.
                        infected[q] = false;
                        infectedTime[q] = -prefs.vars[6];
                        immune[q] = true;
                        currentPeopleImmune++;
                        currentPeopleInfected--;
                    }else if(infectedTime[q] > 0){      //if the infected timer is above 0, ie the person is infected, tick the timer down.
                        infectedTime[q]--;
                    }else if(infectedTime[q] == -1){    //at the end of the immunity duration, make the person not immune anymore, and set their infected timer to a value of 0, signalling that the person is healthy.
                        infectedTime[q] = 0;
                        immune[q] = false;
                    }else if(infectedTime[q] < -1){     //if the infected timer is below -1, ie the person is immune, tick the timer up.
                        infectedTime[q]++;
                    }
                }
                if(printPrefs){     //if they want each data point printed, do so.
                    System.out.println(totalCycles + " cycles " + currentPeopleInfected + " people infected currently " + (prefs.vars[0] - currentPeopleInfected) + " people healthy currently");
                }
                theChart.addNewData(totalCycles, currentPeopleInfected);//update the chart to have the new datapoint corresponding to the current cycle
                long endTime = System.nanoTime();
                long processTime = endTime - startTime;  //the way that this bit of code works is by taking the start time, end time, and, in order to maintain a consistent frame time, waiting
                long timeToWait = FRAME_TIME - processTime;//for the rest of the time required to have a consistent frame time, by taking the desired frame time and subtracting the time 
                                                           //the program took to process. This ensures that the program waits for the same time each time.
                if(timeToWait <= 0){            //if the time to wait is less than or equal to 0 then the simulation is taking longer than allowed to run.
                    System.out.println("uh oh something has gone wrong, the simulation is taking too long");
                    timeToWait = 0;
                }
                timeToWait = timeToWait/1000000; //convert to milliseconds from nanoseconds
                Thread.sleep(timeToWait);       //sleep for the time to wait, so that frame rate is constant
                render();
            } catch (Exception e) {
                System.out.println("An error occurred, inside the tick function");
                e.printStackTrace();
            }
            totalCycles++;
        }else{
            System.out.println("simulation ended after " + totalCycles + " cycles, " + totalPeopleInfected + " people were infected");
            stop();
        }
    }

    private void render(){
        int smileAngle; //initalise the properties of the smiles inside this scope (they are not used anywhere else)
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
        g.clearRect(0, 0, 2500, 2500);              //clear all drawing on the screen

        //draw people
        for(int i = 0; i < prefs.vars[0]; i++){  //loop for all the people
            g.setColor(Color.black);
            g.fillOval(xPos[i], yPos[i], OVAL_DIAM, OVAL_DIAM);     //create the persons body
            if(infected[i]){                                        //this runs through all of the states that the person can be in and makes their smile/face the corresponding color/orientation
                g.setColor(Color.red);          //infected = red
                smileAngle = 0;                 //set smile angle/pos so that it is a sad face
                smileYPos = yPos[i]+25;
                smileWidth = ((OVAL_DIAM*6)/10); //all smile proportions/eye proportions are based on the diameter of the person, for resizability.
                smileHeight = (OVAL_DIAM*4)/12;
            }else if(immune[i]){
                g.setColor(Color.blue);         //immune = blue
                smileAngle = 180;               //set smile angle/pos so that it is a happy face
                smileYPos = yPos[i]+10;
                smileWidth = ((OVAL_DIAM*6)/10);
                smileHeight = (OVAL_DIAM*6)/10;
            }else{
                g.setColor(Color.green);        //healthy = green
                smileAngle = 180;               //set smile angle/pos so that it is a happy face
                smileYPos = yPos[i]+10;
                smileWidth = ((OVAL_DIAM*6)/10);
                smileHeight = (OVAL_DIAM*6)/10;
            }
            g.fillOval(xPos[i] + (OVAL_DIAM/20), yPos[i] + ((OVAL_DIAM/10)/2), OVAL_DIAM*9/10, OVAL_DIAM*9/10);         //draw the oval
            g.setColor(Color.black);
            g.fillOval(xPos[i]+(OVAL_DIAM/4), yPos[i]+(OVAL_DIAM/5), EYE_WIDTH, EYE_HEIGHT);                      //draw the eyes
            g.fillOval(xPos[i]+((OVAL_DIAM*3)/5), yPos[i]+(OVAL_DIAM/5), EYE_WIDTH, EYE_HEIGHT);
            g.drawArc(xPos[i]+(OVAL_DIAM/5), smileYPos, smileWidth, smileHeight, smileAngle, 180);          //draw the smile
        }
        bs.show();
        g.dispose();
    }

    public void run(){
        try{
            render();                                   //when the program starts, render to get the window up on the screen, wait one second and then run.
            Thread.sleep(1000);
        }catch (Exception e) {                          //if there is a error in the first render, then tell the user
            System.out.println("An error occurred, when trying the first render");
            e.printStackTrace();
        }
        while(running){
            tickRun();
        }

        stop();
    }

    private synchronized void start(){
        try {
            initialize();                                   //when you start the program, initialise
        } catch (Exception e) {                             //if there is a error in the initialisation, then tell the user
            System.out.println("An error occurred in the initialisation of the program.");
            e.printStackTrace();
        }
        if(running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();                                     //start the thread
    }

    private synchronized void stop(){                       //stops the program
        if(!running)                                        //if the program is not running, set "running" to false, and try to join the thread.
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
