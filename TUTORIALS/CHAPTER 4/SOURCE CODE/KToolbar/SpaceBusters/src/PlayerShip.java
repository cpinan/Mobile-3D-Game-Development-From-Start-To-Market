/*
 * PlayerShip.java
 *
 * Holds the player's ship object.  Also, we keep track of our ships current life in this class.
 * 
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.game.*;
import javax.microedition.lcdui.game.Sprite;


public class PlayerShip extends Sprite
{
    // member variables
    private int mLife;
    
    /** Creates a new instance of SpaceEnemy */
    public PlayerShip(Image img, int x, int y, int life)
    {
        super(img); // create an non-animated sprite

        // set our initial position sent to us
        this.setPosition(x, y);
        
        // set initial life
        mLife = life;
    }
    
    // Simply draw the ship if we are alive
    public void draw(Graphics g)
    {
        if (mLife > 0)
        {
            this.paint(g);
        }
    }
    
    // Set the life to the amount given
    public void setLife(int amount)
    {
        mLife = amount;
    }

    // Used to determine if the player is alive or dead
    public boolean isAlive()
    {
        if (mLife <= 0)
            return false;
        else
            return true;
    }
}