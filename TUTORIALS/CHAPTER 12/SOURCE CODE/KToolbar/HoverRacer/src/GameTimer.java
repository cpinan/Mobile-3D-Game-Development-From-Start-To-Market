/*
 * GameTimer.java
 *
 * Created on December 23, 2006, 8:39 AM
 *
 * Used to retrieve the exact elapsed time 
 */
import javax.microedition.lcdui.Graphics;

public class GameTimer 
{    
    // These variables hold the time that we
    // retrieve from the system.  We use the long
    // datatype here because the System.currentTimeMillis()
    // returns this type.  We will also be subtracting a 
    // start and ending time. If we cast to floats for size
    // we loose precision and in many cases the time will be 
    // so small of a difference our start and end points will
    // be the same.  So we use longs.
    private static boolean mStarted = false;
    private static boolean mStop = true;
    private static long mStartTime = 0;
    private static long mEndTime = 0;
    
    // the final string of time
    private static String mTime = "0' 0' 0";
    
    // since this is a static class we can simply start our 
    // game timer at any time.  We make sure that we're not
    // already started because we don't want to accidently
    // reset the timer
    public static void start()
    {
        if (!mStarted)
        {
            mStartTime = 0;
            mEndTime = 0;
            
            // we will return this string if desired by user
            // This helps with timing different events during
            // development as well as for showing the player
            // how long he has been player, etc.
            String mTime = "0' 0' 0";
            
            // request the current time in milliseconds
            // which will be our starting time
            mStartTime = System.currentTimeMillis();
            mStop = false;  // we haven't stopped yet!
            mStarted = true; // but we have started!
        }
    }
    
    // each frame this is called and if 
    // we are not stopped we get the current time as 
    // our ending time and we calculate the time 
    public static void update()
    {
        if (mStop != true)
        {
            mEndTime = System.currentTimeMillis();
            mTime = calcTime();
        }
    }
    
    public static void draw(Graphics g)
    {
        // here we draw our time to the screen at our
        // hard coded location.
        g.drawString(mTime, 120, 5, Graphics.TOP
                                   |Graphics.HCENTER);
    }
    
    // This stops the timer.  The last time we updated
    // will be the last time we will have on our timer.
    public static void stop()
    {
        mStop = true;
        mStarted = false;
    }
    
    // This calculates the time in hours, minuites,
    // seconds and milliseconds.  And then it converts
    // the values to a string that can be easily displayed
    private static String calcTime()
    {
        long elapsedTime = mEndTime - mStartTime;
        int hours = (int)(elapsedTime / 3600000);
        if (hours != 0)
            elapsedTime -= 3600000;
        int mins = (int)(elapsedTime / 60000);
        if (mins != 0)
            elapsedTime -= 60000;
        int secs = (int)(elapsedTime / 1000);
        if (secs != 0)
            elapsedTime -= 1000;
        int millis = (int)elapsedTime / 100;
        
        String tmp = mins + "' " + secs + "' " + millis;
        return tmp;
    }
    
    // This returns the time that has elapsed and is
    // the function that is used in our game to time
    // our race.
    public static long getElapsedTime()
    {
        return (mEndTime - mStartTime);
    }
}
