/*
 * SpearEntity.java
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

public class SpearEntity
{
    private static Mesh mMesh;
    private static Transform mTransform;
    
    private static boolean mPickedUp;
    private static boolean mActivated;
    
    /** Creates a new instance of Room */
    public static void initialize(float x, float y, float z)
    {
        // Load our spear entity directly
        //  We can do this because we know that 
        //  the world node is the first node.
        try
        {
            Object3D[] buffer = Loader.load("/Entities/Spear.m3g");
            if(buffer[0] instanceof World)
            {
                World tmpWorld = (World)buffer[0];
                mMesh = (Mesh)tmpWorld.find(5);
            }
        } catch (IOException e) { System.out.println("Unable to load Spear Entity"); }
        
        
        mTransform = new Transform();
        mTransform.postTranslate(x, y, z);
        
        // Turn of lighting
        Appearance appearance = mMesh.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
        
        // we have not picked up this item yet
        mPickedUp = false;
        
        // initially not visible until activated
        mMesh.setRenderingEnable(false);
        mMesh.setPickingEnable(false);
        mActivated = false;
    }
    
    public static void update(Transform playerTrans)
    {
        if (!mPickedUp && mActivated)
        {
            float playerX;
            float playerZ;
            float spearX;
            float spearZ;
            float[] matrix  = new float[16];

            // Check the distance to the player
            // Get the players position
            playerTrans.get(matrix);
            playerX = matrix[3];
            playerZ = matrix[11];

            // Get the spears's position
            mTransform.get(matrix);
            spearX = matrix[3];
            spearZ = matrix[11]; 

            // Get the distance between the player and spear
            float distX = playerX - spearX;
            float distZ = playerZ - spearZ;

            // Check if we hit the spear
            if (Math.abs(distX) < 1.0f && Math.abs(distZ) < 1.0f)
            {        
                mMesh.setRenderingEnable(false);
                mMesh.setPickingEnable(false);
                
                // Game is over and player won!
                SavageGameCanvas.GAME_WON = true;
                mPickedUp = true;
            }

            // rotate our spear object to make it more interesting
            mTransform.postRotate(30.0f, 0, 1, 0);
        }
    }
    
    public static void draw(Graphics3D g3D)
    {
        if (!mPickedUp && mActivated)
            g3D.render(mMesh, mTransform);
    }
    
    public static void activate()
    {
        mActivated = true;
        mMesh.setRenderingEnable(true);
    }
}
