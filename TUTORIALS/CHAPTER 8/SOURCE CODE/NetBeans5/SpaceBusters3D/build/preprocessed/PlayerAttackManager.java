import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class PlayerAttackManager
{
    private static final int MAX_BULLETS = 3;

    private static PlayerBullet[] mPlayerBullets;

    private static long mLastFiredTime = 0;
    private static long mCurrentTime = 0;
    private static long mFireDelay = 300;
    
    /** Creates a new instance of PlayerAttackManager */
    public static void Initialize(PlayerBullet[] bullets)
    {
        mPlayerBullets = bullets;
    }
    
    public static void FireBullet(float x, float y, float z)
    {
        if (mCurrentTime > (mLastFiredTime+mFireDelay))
        {
            for(int i = 0; i < MAX_BULLETS; i++)
            {
                if (mPlayerBullets[i].mAlive == false)
                {
                    // move bullet to firing location and set alive
                    mPlayerBullets[i].SetPosition(x, y, z);
                    mPlayerBullets[i].mAlive = true;
                    mPlayerBullets[i].EnableRender(true);
                    break;
                }
            }
            mLastFiredTime = mCurrentTime;
        }
    }
    
    public static void Update()
    {
        for(int i = 0; i < MAX_BULLETS; i++)
        {
            if (mPlayerBullets[i].mAlive)
                mPlayerBullets[i].Update();
        }
        
        // get current time
        mCurrentTime = System.currentTimeMillis();
    }
    
    // check all bullets against enemies
    public static void CheckBulletsEnemies(SpaceEnemy[] spaceEnemies)
    {
        SpaceEnemy[] enemies = spaceEnemies;
        float[] xyz = new float[3];
        float bulletX = 0.0f;
        float bulletY = 0.0f;
        float bulletZ = 0.0f;
        double distance = 0.0f;
        
        for (int i = 0; i < mPlayerBullets.length; i++)
        {
            if (mPlayerBullets[i].mAlive == true)
            {
                for (int z = 0; z < enemies.length; z++)
                {
                    if (enemies[z].IsAlive())
                    {
                        xyz = mPlayerBullets[i].GetTranslation();
                        bulletX = xyz[0];
                        bulletY = xyz[1];
                        bulletZ = xyz[2];
                        
                        xyz = enemies[z].GetTranslation();
                        
                        // check distance between two objects
                        distance = Math.sqrt( ((xyz[0]-bulletX)
                        * (xyz[0]-bulletX))
                        + ((xyz[1]-bulletY)
                        * (xyz[1]-bulletY))
                        + ((xyz[2]-bulletZ)
                        * (xyz[2]-bulletZ)) );
                        
                        if (distance <= 2.0f)
                        {
                            mPlayerBullets[i].mAlive = false;
                            mPlayerBullets[i].EnableRender(false);
                            enemies[z].DecreaseLife(50);
                            break;
                        }
                    }
                }
            }
        }
    }
}
