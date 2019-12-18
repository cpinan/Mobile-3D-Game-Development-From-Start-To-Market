/*
 * HealthEntity.java
 *
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

public class HealthEntity
{
    private Mesh mMesh;
    private Transform mTransform;
    
    private boolean mPickedUp;
    private boolean mRenderEnabled;
    
    /** Creates a new instance of Room */
    public HealthEntity(float x, float y, float z)
    {
        // Load our health entity directly
        //  We can do this because we know that
        //  the world node is the first node.
        try
        {
            Object3D[] buffer = Loader.load("/Entities/Health.m3g");
            if(buffer[0] instanceof World)
            {
                World tmpWorld = (World)buffer[0];
                mMesh = (Mesh)tmpWorld.find(5);
                
                tmpWorld.removeChild(mMesh);
            }
        }
        catch (IOException e)
        { System.out.println("Unable to load Health Entity"); }
        
        
        mTransform = new Transform();
        mTransform.postTranslate(x, y, z);
        mTransform.postScale(1.5f, 1.5f, 1.5f);
        
        // Turn of lighting
        Appearance appearance = mMesh.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
        
        // we have not picked up this item yet
        mPickedUp = false;
        mRenderEnabled = true;
    }
    
    public void update(Transform playerTrans)
    {
        if (!mPickedUp && mRenderEnabled)
        {
            float playerX;
            float playerZ;
            float healthX;
            float healthZ;
            float[] matrix  = new float[16];
            
            // Check the distance to the player
            // Get the players position
            playerTrans.get(matrix);
            playerX = matrix[3];
            playerZ = matrix[11];
            
            // Get the enemy's position
            mTransform.get(matrix);
            healthX = matrix[3];
            healthZ = matrix[11];
            
            // Get the distance between the player and enemy
            float distX = playerX - healthX;
            float distZ = playerZ - healthZ;
            
            // Check if enemy is to close to player (if so hurt him)
            if (Math.abs(distX) < 1.0f && Math.abs(distZ) < 1.0f)
            {
                mMesh.setRenderingEnable(false);
                mMesh.setPickingEnable(false);
                SavageHUD.resetHealth();
                mPickedUp = true;
            }
            
            // rotate our health object to make it more interesting
            mTransform.postRotate(5.0f, 0, 1, 0);
        }
    }
    
    public void draw(Graphics3D g3D)
    {
        if (!mPickedUp && mRenderEnabled)
            g3D.render(mMesh, mTransform);
    }
    
    public void setPosition(float x, float y, float z)
    {
        float[] matrix = new float[16];
        mTransform.get(matrix);
        matrix[3] = x;
        matrix[7] = y;
        matrix[11] = z;
        mTransform.set(matrix);
    }
    
    public void translate(float x, float y, float z)
    {
        mTransform.postTranslate(x, y, z);
    }
    
    public void scale(float x, float y, float z)
    {
        mTransform.postScale(x, y, z);
    }
    
    public void setID(int value)
    {
        mMesh.setUserID(value);
    }
    
    public Mesh getMesh()
    {
        return mMesh;
    }
    
    public void enableRender(boolean value)
    {
        mRenderEnabled = value;
    }
}
