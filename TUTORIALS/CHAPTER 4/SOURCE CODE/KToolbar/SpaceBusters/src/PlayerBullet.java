/*
 * PlayerBullet.java
 *
 * This is an individual bullet the player fires in the game.  It knows how
 *  to draw and update itself.  The PlayerAttackManager handles the firing
 *  of these bullets.
 *
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.game.*;

public class PlayerBullet extends Sprite
{
    // member variables
    public boolean mAlive;
    private int mDamage;
    private int mSpeed;
    
    /** Creates a new instance of PlayerBullet */
    public PlayerBullet(Image img, int damage) 
    {
        // initialize our class
        super(img);
        
        mDamage = damage;
        
        mAlive = false;
        
        mSpeed = -8;
    }
    
    // We simply draw the sprite wihtout any added effects
    public void draw(Graphics g)
    {
        this.paint(g);
    }
    
    // We first move the sprite up the screen and then test to
    //  see if we are past the top of the screen.
    public void update()
    {
        this.move(0, mSpeed); // shoot up the screen
        if (this.getY() < 0)
            mAlive = false;
    }
}
