/*
 * GunEntity.java
 *
 * Created on December 12, 2006, 7:44 PM
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

public class WeaponEntity 
{
    private Transform mTransform;
    private Mesh mMesh;
    private boolean mPickedUp;
    private boolean mRenderEnabled;
    
    /** Creates a new instance of GunEntity */
    public WeaponEntity(float x, float y, float z) 
    {
        try
        {
            Object3D[] buffer = Loader.load("/Entities/GunBox.m3g");
            if(buffer[0] instanceof World)
            {
                World tmpWorld = (World)buffer[0];
                mMesh = (Mesh)tmpWorld.find(5);

                tmpWorld.removeChild(mMesh);
            }
        } catch (IOException e) { System.out.println("Unable to load Gun Entity"); }
        
        mTransform = new Transform();
        mTransform.postTranslate(x, y, z);
        
        mTransform.postScale(2, 2, 2);
        
         // Turn of lighting
        Appearance appearance = mMesh.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
        
        mPickedUp = false;
        mRenderEnabled = true;
    }
    
    public void update(Transform playerTrans)
    {
        if (!mPickedUp && mRenderEnabled)
        {
            float playerX;
            float playerZ;
            float gunX;
            float gunZ;
            float[] matrix  = new float[16];

            // Check the distance to the player
            playerTrans.get(matrix);
            playerX = matrix[3];
            playerZ = matrix[11];

            // get objects position
            mTransform.get(matrix);
            gunX = matrix[3];
            gunZ = matrix[11]; 

            // Get the distance between the player and gun
            float distX = playerX - gunX;
            float distZ = playerZ - gunZ;

            // Check if should pick up the object
            if (Math.abs(distX) < 1.0f && Math.abs(distZ) < 1.0f)
            {        
                mMesh.setRenderingEnable(false);
                mMesh.setPickingEnable(false);
                SavageHUD.pickUpGun();
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
    
    public void enableRender(boolean value)
    {
        mRenderEnabled = value;
    }
}
