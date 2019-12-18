import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.m3g.Mesh;

public class EnemyManager
{
    private final int MAX_ENEMIES = 9;

    private SpaceEnemy[] mEnemies;
    
    public boolean allEnemiesKilled = false;
    
    /** Creates a new instance of EnemyManager */
    public EnemyManager(SpaceEnemy[] spaceEnemies)
    {
        if (spaceEnemies == null)
        {
            System.err.println("spaceEnemies NULL!");
            return;
        }
        
        mEnemies = spaceEnemies;
        
    }
    
    public void Update()
    {
        int i = 0;
        
        if (mEnemies[0].GetX() < -11.0f)
        {
            for(i = 0; i < MAX_ENEMIES; i++)
            {
                mEnemies[i].SetMoveRight();
                mEnemies[i].MoveDown();
            }
        }
        else if (mEnemies[MAX_ENEMIES-1].GetX() >= 11.0f)
        {
            for(i = 0; i < MAX_ENEMIES; i++)
            {
                mEnemies[i].SetMoveLeft(); // make enemies move left
                mEnemies[i].MoveDown();
            }
        }
        
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            mEnemies[i].Update();
        }
        
        
        // check to see if any alive enemies have hit the ground
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].IsAlive() && mEnemies[i].GetZ() > 7.0f)
            {
                SpaceBusters3DCanvas.mGameOver = true;
            }
        }
        
        for(i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].IsAlive() && mEnemies[i].GetLife() <= 0)
            {
                mEnemies[i].Kill();
                IncreaseSpeed();
            }
        }
        
        // check if all enemies are dead
        if (CheckNumberDead() == MAX_ENEMIES)
        {
            SpaceBusters3DCanvas.mGameOverWin = true;
        }
    }
    
    private int CheckNumberDead()
    {
        int sum = 0;
        for (int i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].IsAlive() == false)
                sum++;
        }
        
        return sum;
    }
    
    private void IncreaseSpeed()
    {
        for(int i = 0; i < MAX_ENEMIES; i++)
        {
            if (mEnemies[i].IsAlive() == true)
                mEnemies[i].mSpeed += 0.2f;
        }
    }
    
    public SpaceEnemy[] GetEnemies()
    {
        return mEnemies;
    }
}
