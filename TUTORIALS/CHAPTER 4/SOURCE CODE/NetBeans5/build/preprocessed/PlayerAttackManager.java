/*
 * PlayerAttackManager.java
 *
 * Managager for all player's attack actions
 *
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.game.*;


public class PlayerAttackManager 
{
    // Constant variables
    private static final int MAX_BULLETS = 5;
    
    // member variables
    private static PlayerBullet[] mPlayerBullets;
   
    private static long mLastFiredTime = 0;
    private static long mCurrentTime = 0;
    private static long mFireDelay = 300;
    
    // Because we have a static class we need ot include this funciton so we 
    //  can 'setup' our class and get it ready to use much like a constructor would
    public static void initialize() 
    {
        try
        {
            // First load the image
            Image bulletImage = Image.createImage("/Player/Bullet.png");
            
            // now create bullet sprites out of the image
            mPlayerBullets = new PlayerBullet[MAX_BULLETS];
            for(int i = 0; i < MAX_BULLETS; i++)
            {
                mPlayerBullets[i] = new PlayerBullet(bulletImage, 20);
            }
        }
        catch(IOException ioe) { System.err.println("Unable to load bullet asset!"); }
    }

    // Each time we fire a bullet we will check to see if a certain delay has passed
    //  This prevents from firing to quickly and having bullets bunched up together.
    public static void fireBullet(int x, int y)
    {
        if (mCurrentTime > (mLastFiredTime+mFireDelay))
        {
            for(int i = 0; i < MAX_BULLETS; i++)
            {
                if (mPlayerBullets[i].mAlive == false)
                {
                    // move bullet to firing location and set alive
                    mPlayerBullets[i].setPosition(x-mPlayerBullets[i].getWidth()/2, y);
                    mPlayerBullets[i].mAlive = true;
                    break;
                }
            }
            mLastFiredTime = mCurrentTime;
        }
    }
    
    // Allow each bullet to update itself as well as get the current time
    // so we can check or firing delay
    public static void update()
    {
        for(int i = 0; i < MAX_BULLETS; i++)
        {
            if (mPlayerBullets[i].mAlive)
                mPlayerBullets[i].update();
        }
        
        // get current time
        mCurrentTime = System.currentTimeMillis();
    }
    
    // Draw each bullet if it's alive
    public static void draw(Graphics g)
    {
        for(int i = 0; i < MAX_BULLETS; i++)
        {
            if (mPlayerBullets[i].mAlive)
                mPlayerBullets[i].draw(g);
        }
    }
    
    // Here we use the sprite's built in collision method to test against
    //  any sprite we send to this function
    public static boolean checkCollisions(Sprite s)
    {
        for(int i = 0; i < MAX_BULLETS; i++)
        {
            if (mPlayerBullets[i].mAlive)
                if (mPlayerBullets[i].collidesWith(s, false))
                {
                    mPlayerBullets[i].mAlive = false;
                    return true; // yes we collided!
                }
        }
        
        // No collision
        return false;
    }
    
}
