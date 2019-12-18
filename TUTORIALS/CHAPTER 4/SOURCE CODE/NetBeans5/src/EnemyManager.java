/*
 * EnemyManager.java
 *
 * Used to manage our enemies.  This tells each enemy to initialize, 
 *  update (logic and AI), and draw
 *
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.game.*;

public class EnemyManager 
{
    // Const variables
    private final int MAX_ENEMIES = 12;
    
    // member variables
    private SpaceEnemy[] mEnemies;
    
    /** Creates a new instance of EnemyManager */
    public EnemyManager() 
    {
        int startX = 0;
        int startY = 0;
        
        try
        {
            // First load our image
            Image enemyImg = Image.createImage("/Enemies/Enemy30x35.png");

            // now create enemies out of our image
            mEnemies = new SpaceEnemy[MAX_ENEMIES];
            for (int i = 0; i < MAX_ENEMIES; i++)
            {
                mEnemies[i] = new SpaceEnemy(enemyImg, startX, startY, 100);
                startX += 35;
                if (startX > SpaceBustersCanvas.SCREEN_WIDTH/2)
                {
                    startX = 0;
                    startY += 35;
                }
            }
        }
        catch(IOException ioe) {System.err.println("Enemy art asset failed loading."); }
    }
    
    // This allows each enemy to update but we will first check the first
    //  and last sprites position.  This is a shortcut that stops us from having
    //  to check every sprites position and then determine how to move the enemies 
    //  on a per sprite basis.  Instead we simply check two.  Also notice that once
    //  an enemy dies it is simply not drawn.  It continues to update and move so 
    //  we can keep checking our sprites position.
    public void update(PlayerShip player)
    {
        int i = 0;
        
        if (mEnemies[0].getX() < 0)
        {
            for(i = 0; i < MAX_ENEMIES; i++)
            {
                mEnemies[i].setMoveRight(); // makes enemies start moving right
                mEnemies[i].moveDown(); // moves enemies down
            }
        }
        else if (mEnemies[MAX_ENEMIES-1].getX() >= SpaceBustersCanvas.SCREEN_WIDTH-32)
        {
            for(i = 0; i < MAX_ENEMIES; i++)
            {
                mEnemies[i].setMoveLeft(); // make enemies move left
                mEnemies[i].moveDown();
            }
        }
            
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            mEnemies[i].update();
        }
        
        // check collisions against the bullets
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].isAlive())
            {
                if (PlayerAttackManager.checkCollisions(mEnemies[i]))
                {
                    mEnemies[i].decreaseLife(20);
                    if (mEnemies[i].isAlive() == false)
                        increaseSpeed();
                }
            }
        }
        
        // check collisions against the player
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].isAlive())
            {
                if (player.isAlive() && player.collidesWith(mEnemies[i], true))
                {
                    player.setLife(0);
                    mEnemies[i].decreaseLife(100);
                }
            }
        }
        
        // check to see if any alive enemies have hit the ground
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].isAlive() && mEnemies[i].getY() > SpaceBustersCanvas.SCREEN_HEIGHT-32)
            {
                player.setLife(0);
            }
        }
        
        // check if all enemies are dead.  If so we've won the game.
        if (checkNumberDead() == MAX_ENEMIES)
            SpaceBustersCanvas.gameWin = true;
    }
    
    // Add up the number of dead enemies
    private int checkNumberDead()
    {
        int sum = 0;
        for (int i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].isAlive() == false)
                sum++;
        }
        
        return sum;
    }
    
    // For added difficulty we will call this to increase the
    //  enemies speed every time one enemy dies.
    private void increaseSpeed()
    {
        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            mEnemies[i].mSpeed += 1;
        }
    }
    
    // Tells each enemy to draw itself
    public void draw(Graphics g)
    {
        for(int i = 0; i < MAX_ENEMIES; i++)
            mEnemies[i].draw(g);
    }
    
}
