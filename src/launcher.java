
/**
 * Write a description of class canvasPath here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import java.util.Scanner;

public class launcher
{
    public launcher() {
        //ImportData data = new ImportData();
        Grapher graph = new Grapher("virus sim", 900, 600);
        graph.start();
    }
}