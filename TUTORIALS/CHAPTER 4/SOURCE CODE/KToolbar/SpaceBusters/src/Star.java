/*
 * Star.java
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

/**
 *
 */
public class Star 
{
    private int mStarSpeed = 0;
    private int mXpos;
    private int mYpos;
    
    /** Creates a new instance of Star */
    public Star(int x, int y) 
    {
        mXpos = x;
        mYpos = y;
    }
    
    public void setSpeed(int amount)
    {
        mStarSpeed = amount;
        if (mStarSpeed <= 0)
            mStarSpeed = 1;
    }
    
    public void moveStar()
    {
        mYpos += mStarSpeed;
    }
    
    public void setPosition(int x, int y)
    {
        mXpos = x;
        mYpos = y;
    }
    
    public int getX()
    {
        return mXpos;
    }
    
    public int getY()
    {
        return mYpos;
    }
    
    public void drawStar(Graphics g)
    {    
        int oldColor = g.getColor();
        
        g.setColor(255, 255, 255);
        g.drawLine(mXpos, mYpos, mXpos, mYpos);
        g.setColor(oldColor);
    }
}// end of classs