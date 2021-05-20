//@author Luke Weston
//* @version 6.0
import java.util.concurrent.ThreadLocalRandom;
public class Person
{
   int xPos;
   int yPos;
   boolean infected = false;
   int direction;
   int healTime;
   int infectedTimer = 0;
   int immuneTimer = 0;
   boolean amIPatientZero = false;
   public Person(int width, int height)
   {
        xPos = ThreadLocalRandom.current().nextInt(0, width);
        yPos = ThreadLocalRandom.current().nextInt(0, height);
        direction = ThreadLocalRandom.current().nextInt(0, 4);
   }
}