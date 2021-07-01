package src;//@author Luke Weston
//* @version 11.0
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
public class VirusSim {
    Scanner input = new Scanner(System.in);
    public VirusSim() throws Exception{
        int peopleInfected = 0;
        System.out.println("note: infected patients cannot be reinfected, and so cannot reset their timer");
        System.out.println("note: enter -1 for default value, width/height cannot be less than 3.");
        System.out.println("presets are: population size of 50, world width of 10, world height of 10, run for 10 days, 10 people to start infected,");
        System.out.println("people are infected for 10 days before being cured, and are immune for 3 days after being cured, repeat simulation 20 times.");
        String[] prompts = new String[] {"enter population", "enter width of world", "enter height of world", "enter days to run", "enter number of people to start as infected", 
                "enter how long people are infected for", "enter how long people are immune for after they are cured", "enter how many times to repeat"};
        File file = new File ("output.txt");
        Preferences prefs = new Preferences();
        for(int z=0; z<8; z++){
            System.out.println(prompts[z]);
            int temp = input.nextInt();
            if(temp != -1){
                prefs.vars[z] = temp;
            }
        }
        String[][] world = new String[prefs.vars[1]][prefs.vars[2]];
        FileWriter writer = new FileWriter(file);
        for(int y=0; y<prefs.vars[7]; y++){
            peopleInfected = 0;
            peopleInfected = peopleInfected++;
            Person[] people = new Person[prefs.vars[0]];
            for(int i=0; i<prefs.vars[0]; i++){
                people[i] = new Person(prefs.vars[1], prefs.vars[2]-1);
            }
            for(int q=0; q<prefs.vars[4]; q++){
                people[1].infected = true;
                peopleInfected++;
            }

            int daysRan = 0;
            while(daysRan < prefs.vars[3]){
                for(int i=0; i<prefs.vars[0]; i++){
                    //if (people[i].xPos >= prefs.vars[1] || people[i].xPos <= 0 || people[i].yPos >= prefs.vars[2] || people[i].yPos <= 0){
                    //    running = false;
                    //}

                    for(int k=0; k<prefs.vars[1]; k++){
                        for(int l=0; l<prefs.vars[2]; l++){
                            world[k][l] = "_";
                        }
                    }
                    if (people[i].direction == 0){
                        if(people[i].xPos + 1 >= prefs.vars[1]){
                            people[i].direction = 2;
                            people[i].xPos--;
                        }else{
                            people[i].xPos++;
                        }
                    }
                    if(people[i].direction == 1){
                        if(people[i].xPos - 1 <= 0){
                            people[i].direction = 0;
                            people[i].xPos++;
                        }else{
                            people[i].xPos--;
                        }
                    }
                    if (people[i].direction == 2){
                        if(people[i].yPos + 1 >= prefs.vars[2]){
                            people[i].direction = 3;
                            people[i].yPos--;
                        }else{
                            people[i].yPos++;
                        }
                    }else if(people[i].direction == 3){
                        if(people[i].yPos - 1 <= 0){
                            people[i].direction = 1;
                            people[i].yPos++;
                        }else{
                            people[i].yPos--;
                        }
                    }
                    //if (people[i].xPos >= prefs.vars[1] || people[i].xPos <= 0 || people[i].yPos >= prefs.vars[2] || people[i].yPos <= 0){
                    //  running = false;
                    //}
                    people[i].direction = ThreadLocalRandom.current().nextInt(0, 4);
                }
                for(int p=0; p<prefs.vars[0]; p++){
                    if(people[p].infected){
                        world[people[p].xPos][people[p].yPos] = "J"; 
                    }else{
                        world[people[p].xPos][people[p].yPos] = "A";
                    }
                    for(int u=0;u<prefs.vars[0];u++){
                        if(people[p].xPos == people[u].xPos && people[p].yPos == people[u].yPos && people[p].infected &&  people[u].infected == false && people[u].infectedTimer == 0){
                            if(p != u){
                                people[u].infected = true;
                                peopleInfected++;
                                people[u].infectedTimer = prefs.vars[5] + 1;
                                //System.out.println("day " + daysRan + ", " + peopleInfected + " people infected");

                            }
                        }
                    }
                    if(people[p].infectedTimer > 0){
                        people[p].infectedTimer--;
                        if(people[p].infectedTimer == 0){
                            people[p].immuneTimer = prefs.vars[6] + 1;
                            peopleInfected--;
                        }
                    }
                    if(people[p].infectedTimer > 0){
                        people[p].immuneTimer--;
                    }
                }

                // for(int h=0; h<prefs.vars[1]; h++){
                // for(int g=0; g<prefs.vars[2]; g++){
                // System.out.print(world[h][g] + " ");
                // }
                // System.out.print("\n");
                // }
                // System.out.print("\n");
                // System.out.print("\n");
                daysRan++;

            }
            System.out.print("\n");
            System.out.println("day " + daysRan + ", " + peopleInfected + " people infected");
            writer.write(daysRan + " " + peopleInfected + " " + daysRan/peopleInfected + "\n");

        }
        writer.write("daysRan peopleInfected daysRan/peopleInfected" + "\n");
        writer.flush();
        writer.close();
    }
}