/*
 * Room.java
 *
 * Created on November 3, 2006, 6:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Texture2D;


public class Room
{
    
    private Mesh mMesh;
    private Transform mTransform;
    
    private boolean mRenderMesh;
    
    private float mTopLeftX;
    private float mTopLeftZ;
    private float mBottomRightX;
    private float mBottomRightZ;
    
    /** Creates a new instance of Room */
    public Room(Mesh mesh, float tx, float tz, float bx, float bz) 
    {
        mMesh = mesh;

        mTransform = new Transform();
        mMesh.getTransform(mTransform);
 
        // for now positon info is provided for us
        mTopLeftX = tx;
        mTopLeftZ = tz;
        mBottomRightX = bx;
        mBottomRightZ = bz;
                
        // Turn of lighting
        Appearance appearance = mMesh.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
    }
    
    // Checks to see if player is in this room
    public boolean isIn(float x, float z)
    {
        if ( (x < mTopLeftX && x > mBottomRightX) && (z > mTopLeftZ && z < mBottomRightZ) )
            return true;
       
        return false;
    }
    
    public void draw(Graphics3D g3D)
    {
        if (mRenderMesh)
            g3D.render(mMesh, mTransform);
    }
    
    public void enableRender(boolean value)
    {
        mRenderMesh = value;
    }
    
    public void enablePick(boolean value)
    {
        mMesh.setPickingEnable(value);
    }
    
    public boolean renderMesh()
    {
        return mRenderMesh;
    }
    
    public Mesh getMesh()
    {
        return mMesh;
    }
}
