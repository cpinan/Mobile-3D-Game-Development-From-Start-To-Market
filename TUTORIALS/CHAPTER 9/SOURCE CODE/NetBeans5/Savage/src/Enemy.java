/*
 * Enemy2.java
 *
 * Created on December 13, 2006, 10:07 PM
 *
 */
import java.io.*;
import javax.microedition.m3g.SkinnedMesh;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.World;

public class Enemy
{
    private SkinnedMesh mMesh;
    private Transform mTransform;
    private float[] mStartXYZ;
    private int mLife;
    private boolean mAlive;
    private boolean mDrawDeath;
    private int mDeathCounter;
    private int mAnimateTime;
    private int mNumDeaths;
    
    public static final int ENEMYSTATE_DEAD = 0;
    public static final int ENEMYSTATE_ALIVE = 1;
    public static final int ENEMYSTATE_DISABLED = 2;
    private static final int ENEMYSTATE_ATTACK = 3;
    private static final int ENEMYSTATE_ATTACKRUN = 4;
    
    private int mEnemyState;
    private int mEnemyAttackState;
    
    private long mCurrentTime;
    private long mLastAttackTime;
    private int mAttackDelay = 300; // when we can attack again
    
    public Enemy(float x, float y, float z)
    {
        // load the enemy
        try
        {
            Object3D[] buffer = Loader.load("/Enemy/RaptorWalk.m3g");
            if(buffer[0] instanceof World)
            {
                World tmpWorld = (World)buffer[0];
                mMesh = (SkinnedMesh)tmpWorld.find(56);
                
                // remove the mesh's parent and add
                //  to our own world group so we can
                //  collide with the enemy
                tmpWorld.removeChild(mMesh);
                SavageGameCanvas.worldGroup.addChild(mMesh);
            }
        }
        catch (IOException e)
        { System.out.println("Unable to load Enemy"); }
        
        mTransform = new Transform();
        mTransform.postTranslate(x, y, z);
        mTransform.postScale(2, 2, 2);
        
        mStartXYZ = new float[3];
        mStartXYZ[0] = x;
        mStartXYZ[1] = y;
        mStartXYZ[2] = z;
        
        mLife = 60;
        mEnemyState = ENEMYSTATE_DEAD;
        mEnemyAttackState = ENEMYSTATE_ATTACK;
        mDrawDeath = true;
        mDeathCounter = 0;
        mAnimateTime = 0;
        mNumDeaths = 0;
        
        // turn off lighitng
        Appearance appearance = mMesh.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
    }
    
    public void update(Transform playerTrans)
    {
        if (mEnemyState == ENEMYSTATE_DEAD)
        {
            mDeathCounter++;
            if (mDeathCounter > 50)
            {
                mDrawDeath = false;
                mEnemyState = ENEMYSTATE_DISABLED;
            }
            
        }
        else if (mEnemyState == ENEMYSTATE_ALIVE)
        {
            float[] matrix = new float[16];
            float[] playerXYZ = new float[3];
            float[] enemyXYZ = new float[3];
            
            // Get the players position
            playerTrans.get(matrix);
            playerXYZ[0] = matrix[3];
            playerXYZ[1] = matrix[7];
            playerXYZ[2] = matrix[11];
            
            // Get the enemy's position
            mTransform.get(matrix);
            enemyXYZ[0] = matrix[3];
            enemyXYZ[1] = matrix[7];
            enemyXYZ[2] = matrix[11];
            
            // Get the distance between the player and enemy
            float[] distXYZ = new float[3];
            distXYZ[0] = enemyXYZ[0] - playerXYZ[0];
            distXYZ[1] = enemyXYZ[1] - playerXYZ[1];
            distXYZ[2] = enemyXYZ[2] - playerXYZ[2];
            
            // check distance to player.  Hurt if necessary
            // also stop from getting to close to player
            if (Math.abs(distXYZ[0]) < 1.0f && Math.abs(distXYZ[2]) < 1.0f)
            {
                // check when the last time we attacked
                //  the player was.  Only attack
                //  if we are past our attack delay
                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime > (mLastAttackTime+mAttackDelay))
                {
                    // hurt player
                    SavageHUD.hurt();
                    
                    // we will request the hardware
                    //  to flash the screen and
                    //  vibrate for a feedback effect
                    SavageGameCanvas.vibrateAndFlash(500);
                    
                    // jump back out of the attack range
                    //  so we don't kill him with one hit
                    mTransform.postTranslate(0, 0, -3);
                    
                    // now switch to run away mode to make
                    //  it more interesting
                    mEnemyAttackState = ENEMYSTATE_ATTACKRUN;
                    
                    // set our last attacked time
                    mLastAttackTime = mCurrentTime;
                }
            }
            
            // normalize this so we can use it in our calculations
            vectorNormalize(distXYZ);
            
            // create a new up vector and set its values to the up vector
            float[] up = new float[3];
            up[0] = matrix[1];
            up[1] = matrix[5];
            up[2] = matrix[9];
            
            // create a new empty right vector and use a cross product between
            //  the up and distance vector
            float[] right = new float[3];
            vectorCrossProduct(distXYZ, up, right);
            
            // set our right vector
            matrix[0] = right[0];
            matrix[4] = right[1];
            matrix[8] = right[2];
            
            // set our new forward vector
            //  We leave out the y coordinate becuse we are doing
            //  a raycast from different y position values.  We do
            //  not want to move up at all so we only move on the X-Z plane.
            matrix[2] = -1*distXYZ[0];
            matrix[10]= -1*distXYZ[2];
            
            // now set the matrix that we inserted our new right and look vectors in
            mTransform.set(matrix);
            
            if (mEnemyAttackState == ENEMYSTATE_ATTACK)
            {
                // Move the enemy forward towards player
                mTransform.postTranslate(0, 0, 0.1f);
            }
            else if (mEnemyAttackState == ENEMYSTATE_ATTACKRUN)
            {
                mTransform.postTranslate(0, 0, -0.4f);
            }
            
            // now make sure we are in bounds inside the room
            if (enemyXYZ[0] > 3 || enemyXYZ[0] < -9)
            {
                // jump enemy away from the wall
                mTransform.postTranslate(0, 0, 2);
                
                // switch states so enemy will go
                //  a diff diection
                switchStates();
            }
            
            if (enemyXYZ[2] > 9.5f || enemyXYZ[2] < -9)
            {
                // jump enemy away from the wall
                mTransform.postTranslate(0, 0, 2);
                
                // switch states so enemy will go
                //  a diff diection
                switchStates();
            }
            
            mMesh.animate(mAnimateTime+=50);
            if (mAnimateTime >= 1000)
                mAnimateTime = 0;
        }
    }
    
    public void draw(Graphics3D g3D)
    {
        if (mEnemyState == ENEMYSTATE_ALIVE || mDrawDeath)
            g3D.render(mMesh, mTransform);
    }
    
    public int getState()
    {
        return mEnemyState;
    }
    
    public boolean drawDeath()
    {
        return mDrawDeath;
    }
    
    public void switchStates()
    {
        if (mEnemyAttackState == ENEMYSTATE_ATTACK)
            mEnemyAttackState = ENEMYSTATE_ATTACKRUN;
        else
            mEnemyAttackState = ENEMYSTATE_ATTACK;
    }
    
    public void enable(boolean value)
    {
        if (value)
        {
            if (mLife > 0)
                mEnemyState = ENEMYSTATE_ALIVE;
        }
        else
            mEnemyState = ENEMYSTATE_DISABLED;
    }
    
    public void decreaseLife(int amount)
    {
        if (mEnemyState == ENEMYSTATE_ALIVE)
        {
            mLife -= amount;
            if (mLife <= 0)
            {
                mEnemyState = ENEMYSTATE_DEAD;
                
                // flip enemy on it's side and
                //  translate to the ground
                mTransform.postRotate(90, 0, 0, 1);
                mTransform.postTranslate(0, -2, 0);
                
                mDrawDeath = true;
                mDeathCounter = 0;
                
                mNumDeaths++;
            }
        }
    }
    
    public void reset()
    {
        if (mNumDeaths < 2)
        {
            mTransform.setIdentity();
            mTransform.postTranslate(mStartXYZ[0], mStartXYZ[1], mStartXYZ[2]);
            mTransform.postScale(2, 2, 2);
            
            mLife = 60;
            mEnemyState = ENEMYSTATE_ALIVE;
            mEnemyAttackState = ENEMYSTATE_ATTACK;
            mDrawDeath = true;
            mDeathCounter = 0;
        }
        else
            SpearEntity.activate();
    }
    
    
    private float vectorNormalize(float vector[])
    {
        
        // find the length of the vector given to us
        float vecLength =(float)Math.sqrt((vector[0]*vector[0])
        + (vector[1]*vector[1]) + (vector[2]*vector[2]));
        
        // make sure we're not already normalized
        if(vecLength > 0.0f)
        {
            // here we will multiply because it is faster than dividing
            float divisor = 1 / vecLength;
            vector[0] *= divisor;
            vector[1] *= divisor;
            vector[2] *= divisor;
        }
        return vecLength;  // return our normalized vector's length
    }
    
    private void vectorCrossProduct(float vector1[], float vector2[], float vecOutput[])
    {
        if(vecOutput == vector1 || vecOutput == vector2)
        {
            // calc our cross prodcut
            float x = (vector1[1]*vector2[2]) - (vector1[2]*vector2[1]);
            float y = (vector1[2]*vector2[0]) - (vector1[0]*vector2[2]);
            
            // Now set our values
            vecOutput[0]=y;
            vecOutput[1]=x;
            vecOutput[2]=(vector1[0] * vector2[1]) - (vector1[1] * vector2[0]);
            
        }
        else
        {
            // set the calculated crossproduct values
            vecOutput[0]=(vector1[1]*vector2[2])-(vector1[2]*vector2[1]);
            vecOutput[1]=(vector1[2]*vector2[0])-(vector1[0]*vector2[2]);
            vecOutput[2]=(vector1[0]*vector2[1])-(vector1[1]*vector2[0]);
        }
    }
}
