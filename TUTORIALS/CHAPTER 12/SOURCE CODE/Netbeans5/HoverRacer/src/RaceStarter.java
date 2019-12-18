/*
 * RaceStarter.java
 *
 * Created on December 24, 2006, 12:39 PM
 *
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;

// This class can be a little confusing.  The purpose is to
// handle the starting light that will count down the time
// until it the palyer is supposed to drive.  Also, if the
// light is not a good enough sign at actual start the words
// GO! are displayed for a couple seconds.  And last this 
// class is responsible for letting the player start moving
// when appropriate as well as keeping track of the elapsed
// time that has passed by.  We'll use this time to compare
// the two racers speed to find a winner.
public class RaceStarter 
{
    // Create our starting light, go sprite, and
    // other basic objects and properties
    private static StartingLight mStartingLight;
    private static Sprite mGo;
    private static String mPlayerTime;
    private static boolean mMovementEnabled;
    
    // this holds how long the words GO are displayed
    // on the screen
    private static int mGoDisplayCounter;
    
    public static void initialize()
    {
        try
        {
            // load all our 2D objects
            mGo = new Sprite(Image.createImage("/menus/go.png"));
            mGo.setPosition(120-mGo.getWidth()/2, 160-mGo.getHeight());
            mGoDisplayCounter = 0;
            
            mStartingLight = new StartingLight(Image.createImage("/menus/lights50x100.png"), 50, 100);
            mStartingLight.setPosition(240/2-mStartingLight.getWidth()/2, 10);
            
            mPlayerTime = "";
        } catch(IOException e) { System.out.println("Error loading RaceStarter resources."); }
    }
    
    // This is true if the race has started and the 
    // player can start trying to increase his speed
    public static boolean movementEnabled()
    {
        return mMovementEnabled;
    }
    
    // The game starts by the starting light switching
    // lights till the bottom.
    public static void start()
    {
        mStartingLight.activate();
    }
    
    // here we update the different events (starting light,
    // GO! sprite, and GameTimer object)
    public static void update()
    {
        if (mStartingLight.getGo() == false)
        {
            mStartingLight.update();
        }
        else
        {
            // Update the timer so we know how long has passed
            // since the start
            GameTimer.start();
            mMovementEnabled = true;
            
            mGoDisplayCounter++;
            if (mGoDisplayCounter > 30)
            {
                // no longer draw the worlds GO!
                mStartingLight.setGo(false);
            }
        }
    }
    
    // This draws the different objects on the screen
    // depending on what state of starting the game is in
    public static void draw(Graphics g)
    {
        mStartingLight.draw(g);
        
        if (mStartingLight.getGo() == true)
            mGo.paint(g);
        
        GameTimer.draw(g);
    }
}
