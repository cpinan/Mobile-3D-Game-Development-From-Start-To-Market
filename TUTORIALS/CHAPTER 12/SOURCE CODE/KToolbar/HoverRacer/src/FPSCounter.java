/*
 * FPSCounter.java
 *
 * Created on October 23, 2006, 6:12 PM
 *
 */
import javax.microedition.lcdui.Graphics;

public class FPSCounter
{   
    private static int mFPSResult = 0;
    private static int mFPSCounter = 0;
    private static long mTotalTime = 0;
    private static long mStartTime = 0;
        
    // Initializes the Frames Per Second counter
    public static void initialize()
    {
        mFPSResult = 0;
        mFPSCounter = 0;
        mTotalTime = 0;
        
        mStartTime = System.currentTimeMillis();
    }
    
    public static void update()
    {
        long currentTime = System.currentTimeMillis();
        long frameTime = currentTime - mStartTime;
        mStartTime = currentTime;
    
        mTotalTime += frameTime;
        if (mTotalTime >= 1000)
        {
            mFPSResult = mFPSCounter + 1;
            mFPSCounter = 0;
            mTotalTime = 0;
        }
        mFPSCounter++;
    }
    
    public static void draw(Graphics g)
    {
        g.setColor(0, 0, 255);
        g.drawString("[ " + mFPSResult + " ]", 0, 280, Graphics.TOP | Graphics.LEFT);
    }
}
