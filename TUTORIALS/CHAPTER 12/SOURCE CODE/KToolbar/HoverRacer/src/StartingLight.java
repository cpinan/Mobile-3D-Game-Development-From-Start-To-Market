/*
 * StartingLight.java
 *
 * Created on December 24, 2006, 8:17 AM
 *
 */
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

// This class is just a way to let our sprite slowly
// switch frames as well as display the words GO! when
// the last light is lit.  This is nothing complex
// and we have seen this all before starting way back
// in chapter 4.  The mGo paramater is used to tell when
// to draw the sprite with the words GO! on it.
public class StartingLight extends Sprite
{
    private boolean mStart;
    private boolean mGo;
    private int mCounter;
        
    public StartingLight(Image img, int cellWidth, int cellHeight)
    {
        super(img, cellWidth, cellHeight);
        
        mStart = false;
        mCounter = 0;
    }
    
    public void update()
    {
        if (mStart)
        {
            mCounter++;
            if ((mCounter % 20) == 0)
            {
                nextFrame();
                if (getFrame() == 0)
                {
                    mStart = false;
                    mGo = true;
                }
            }
        }
    }
    
    public void draw(Graphics g)
    {
        if (mStart == true)
            paint(g);
    }
    
    public void activate()
    {
        mStart = true;
    }
    
    public boolean getGo()
    {
        return mGo;
    }
    
    public void setGo(boolean value)
    {
        mGo = value;
    }
}
