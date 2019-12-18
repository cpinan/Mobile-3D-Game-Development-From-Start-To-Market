/*
 * SpaceEnemy.java
 *
 * This is our basic enemy class.  It holds the sprite, life, speed, 
 *  and direction of the enemy itself. It also hanles the basic movement
 *  of the enemy to the left and right.
 *
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.game.*;

public class SpaceEnemy extends Sprite
{
    // member varialbes
    public int mSpeed = 1;
    private boolean mAlive;
    private int mLife;
    private int mMoveDir;
    
    /** Creates a new instance of SpaceEnemy */
    public SpaceEnemy(Image img, int x, int y, int life)
    {
        // setup our inital values
        super(img, 30, 35); // An alternative to hard-coding this is to add
                            //  two more variables to our constructor to give
                            //  us the animation cell size.  For now we'll 
                            //  hard code it.

        setPosition(x, y);
        mMoveDir = 1;
        mLife = life;
        mAlive = true;
    }
    
    // We check to see if we are alive.  We then move to the right or left.
    //  Notice that even if we are dead we still get called to move.  This
    //  allows us to cheat by checking the positions of only two sprites
    //  instead of all of them.  (See EnemyManager.java)
    public void update()
    {
        if (mLife <= 0)
            mAlive = false;
        // update to keep the enemies in sync
        move(mMoveDir*mSpeed, 0);
        nextFrame(); // next frame of animation
    }
    
    // If we are alive we draw the sprite.  If not alive we do not draw.
    public void draw(Graphics g)
    {
        if (mAlive)
            paint(g);
    }
    
    // We multiply our mMoveDir times our speed.  If we set it to -1 we go left
    public void setMoveLeft()
    {
        mMoveDir = -1;
    }
    
    // We multiply our mMoveDir times our speed.  If we set it to 1 we go right
    public void setMoveRight()
    {
        mMoveDir = 1;
    }
    
    // Moves our enemies down 10 units (Called by EnemyManager.java)
    public void moveDown()
    {
        move(0, 10);
    }
    
    // Decreases the life of the enemy and allows for it to be shot more than once
    public void decreaseLife(int amount)
    {
        mLife -= amount;
        if (mLife <= 0)
            mAlive = false;
    }
    
    // Allows us to check to see if this enemy is alive
    public boolean isAlive()
    {
        return mAlive;
    }
}
