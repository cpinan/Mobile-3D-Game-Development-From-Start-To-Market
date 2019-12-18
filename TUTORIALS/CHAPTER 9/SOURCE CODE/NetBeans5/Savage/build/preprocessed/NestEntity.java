/*
 * NestEntity.java
 *
 * Created on December 13, 2006, 2:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.io.*;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.World;
import javax.microedition.m3g.Texture2D;

public class NestEntity
{
    private Mesh mMesh;
    private Transform mTransform;
    
    private boolean mActivated;
    private float mDelay;
    
    private long mCurrentTime;
    private long mLastSpawnedTime;
    
    private Enemy mEnemy;
    private int mNumSpawned;
    
    private boolean mRenderEnabled;
    
    /** Creates a new instance of Room */
    public NestEntity(float x, float y, float z, float delay)
    {
        // Load our nest entity directly
        //  We can do this because we know that
        //  the world node is the first node.
        try
        {
            Object3D[] buffer = Loader.load("/Entities/Nest.m3g");
            if(buffer[0] instanceof World)
            {
                World tmpWorld = (World)buffer[0];
                mMesh = (Mesh)tmpWorld.find(5);
            }
        }
        catch (IOException e)
        { System.out.println("Unable to load Nest Entity"); }
        
        
        mTransform = new Transform();
        mTransform.postTranslate(x, y, z);
        mTransform.postScale(2, 2, 2);
        
        // Turn of lighting
        Appearance appearance = mMesh.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
        
        mActivated = false;
        mNumSpawned = 0;
        
        mEnemy = new Enemy(x, -2.0f, z);
        
        mRenderEnabled = true;
    }
    
    public void update(Transform playerTrans)
    {
        if (mRenderEnabled)
        {
            if (mActivated)
            {
                mEnemy.update(playerTrans);
                
                if (mEnemy.getState() == Enemy.ENEMYSTATE_DISABLED
                        && mEnemy.drawDeath() == false)
                    mEnemy.reset();
            }
        }
    }
    
    public void draw(Graphics3D g3D)
    {
        if (mRenderEnabled)
        {
            g3D.render(mMesh, mTransform);
            
            if (mActivated)
                mEnemy.draw(g3D);
        }
    }
    
    public void activate()
    {
        if (mActivated == false)
        {
            // start spawning enemy
            mActivated = true;
            mEnemy.enable(true);
        }
    }
    
    public boolean getState()
    {
        return mActivated;
    }
    
    public void deactivate()
    {
        // no longer spawn enemies
        mActivated = false;
    }
    
    public int getNumSpawned()
    {
        return mNumSpawned;
    }
    
    public void hurtEnemy()
    {
        if (mActivated)
            mEnemy.decreaseLife(20);
    }
    
    public void enableRender(boolean value)
    {
        mRenderEnabled = value;
    }
}
