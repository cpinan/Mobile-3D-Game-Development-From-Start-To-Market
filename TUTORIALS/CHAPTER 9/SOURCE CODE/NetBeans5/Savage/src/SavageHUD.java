/*
 * SavageHUD.java
 *
 * Created on November 27, 2006, 3:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author David Nelson
 */
public class SavageHUD
{
    private static Sprite mLifeBar;
    private static Sprite mHealthBar;
    private static Sprite mCrosshairs;
    private static Sprite mGun;
    private static boolean mHasWeapon;
    
    public static void Initialize()
    {
        try
        {
            mLifeBar = new Sprite(Image.createImage("/HUD/LifeBar39x14.png"), 39, 14);
            mHealthBar = new Sprite(Image.createImage("/HUD/HealthBar100x14.png"), 100, 14);
            mCrosshairs = new Sprite(Image.createImage("/HUD/crosshairs32x32.png"));
            mGun = new Sprite(Image.createImage("/HUD/gun.png"), 128, 128);
            mGun.setVisible(false);
        }
        catch(Exception e)
        { System.out.println("HUD Loading error: " + e.getMessage()); }
        
        mLifeBar.setPosition(10, 10);
        mHealthBar.setPosition(SavageGameCanvas.SCREEN_WIDTH-mHealthBar.getWidth()-10, 10);
        mCrosshairs.setPosition(SavageGameCanvas.SCREEN_WIDTH/2-mCrosshairs.getWidth()/2, SavageGameCanvas.SCREEN_HEIGHT/2-mCrosshairs.getHeight()/2);
        mGun.setPosition(SavageGameCanvas.SCREEN_WIDTH - mGun.getWidth(), SavageGameCanvas.SCREEN_HEIGHT-mGun.getHeight());
        
        // Now setup our starting values for our game
        mHealthBar.setFrame(4);
        mLifeBar.setFrame(2);
    }
    
    public static void draw(Graphics g)
    {
        mLifeBar.paint(g);
        mHealthBar.paint(g);
        mCrosshairs.paint(g);
        mGun.paint(g);
    }
    
    public static Sprite getGun()
    {
        return mGun;
    }
    
    public static void hurt()
    {
        mHealthBar.nextFrame();
        if (mHealthBar.getFrame() == 5)
            takeLife();
    }
    
    public static void resetHealth()
    {
        // full health!
        mLifeBar.setFrame(0);
        mHealthBar.setFrame(0);
    }
    
    private static void takeLife()
    {
        mLifeBar.nextFrame();
        if (mLifeBar.getFrame() == 3)
            SavageGameCanvas.GAME_OVER = true;
        
        mHealthBar.setFrame(0);
    }
    
    public static void pickUpGun()
    {
        mGun.setVisible(true);
        mHasWeapon = true;
    }
    
    public static boolean hasWeapon()
    {
        return mHasWeapon;
    }
}
