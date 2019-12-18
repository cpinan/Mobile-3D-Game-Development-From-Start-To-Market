/*
 * StarManager.java
 *
 * This is a neat little class that shows off a decent starfield
 *  Explore this class and see if you can determine how it works.
 *  We should be able to note how close this is to all of our 
 *  managers and individual objects. 
 *
 */
 
import java.util.*;
import java.io.*;
import javax.microedition.lcdui.*;

public class StarManager 
{
    private static final int MAX_STARS = 100;
    
    private Random mRand;
    private Star[] mStarField;    
    
    /** Creates a new instance of StarManager */
    public StarManager() 
    {
        mRand = new Random();
        
        mStarField = new Star[MAX_STARS];
    
        for (int i = 0; i < MAX_STARS; i++)
        {
            mStarField[i] = new Star(mRand.nextInt() % SpaceBustersCanvas.SCREEN_WIDTH, mRand.nextInt() % SpaceBustersCanvas.SCREEN_HEIGHT);
            mStarField[i].setSpeed(mRand.nextInt(10) + 2);
        }
    }
    
    public void update()
    {
        // move the stars
        for (int q = 0; q < MAX_STARS; q++)
        {
            mStarField[q].moveStar();
            
            // reset the stars when they reach bottom
            if (mStarField[q].getY() > SpaceBustersCanvas.SCREEN_HEIGHT)
            {
                mStarField[q].setPosition(mRand.nextInt() % SpaceBustersCanvas.SCREEN_WIDTH, 0);
            }
        }
    }
    
    public void drawStars(Graphics g)
    {
        for (int i = 0; i < MAX_STARS; i++)
            mStarField[i].drawStar(g);
    }
}