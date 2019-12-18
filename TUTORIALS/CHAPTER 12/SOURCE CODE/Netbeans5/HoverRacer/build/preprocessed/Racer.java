/*
 * Racer.java
 *
 * Created on December 21, 2006, 11:26 AM
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
import javax.microedition.m3g.RayIntersection;


// As the name implies, this holds our hover racer object.
// Even though the player 'selects' his own car, they really
// are the same car with different textures.  They are not
// any different in preformance (but this would be an 
// excellent excersise to add to this project!)
public class Racer 
{
    // We hold our mesh, transform, textures, and 
    // speed properties
    private Mesh mMesh;
    private Transform mTransform;
    private Texture2D mTexture1;
    private Texture2D mTexture2;
    private Texture2D mTexture3;
    private int mTextureIndex;
    private Appearance mAppearance;
    private float mSpeed;
    
    public Racer(Mesh mesh, Texture2D texture2, Texture2D texture3, float x, float y, float z) 
    {
        // Store the mesh and different possible textures
        // for our hover racer.  We also store the texutre
        // that is currently on the mesh so that we can 
        // return to it later if the player chooses to
        mMesh = mesh;
        mTexture1 = mMesh.getAppearance(0).getTexture(0);
        mTexture2 = texture2;
        mTexture3 = texture3;
        
        // This is the transform of the object and the defined
        // starting position is set
        mTransform = new Transform();
        mTransform.postTranslate(x, y, z);
        
        // this tells us what texture we are
        // currently on so we can move to the next
        // or return to the first if desired
        mTextureIndex = 0;
        
        // this is the initial speed of the hover racer
        mSpeed = 0.3f;        
        
        // We must turn off lighting if we want to see it in game!
        mAppearance = mMesh.getAppearance(0);
        Texture2D tex = mAppearance.getTexture(0);
        tex.setBlending(Texture2D.FUNC_REPLACE);
    }

    // sets the speed of the racer
    public void setSpeed(float value)
    {
        mSpeed = value;
    }
    
    // we use this to increase the speed
    // of the racer.  We try and cap it 
    // at a max of 1
    public void increaseSpeed()
    {
        mSpeed += 0.08f;
        if (mSpeed >= 1)
            mSpeed = 1;
    }
    
    // When the player lets go of the FIRE key then 
    // we slow down the player considerably fast but 
    // we do not let it go below Zero (racer would
    // move backwards).  We also add friction when
    // the player hits the wall.
    public void addFriction()
    {
        mSpeed -= 0.03;
        if (mSpeed <= 0)
            mSpeed = 0;
    }
    
    // When selecting their car they switch textures
    // in order to give them a personalized feel to the game.
    public void nextTexture()
    {
        mTextureIndex++;
        if (mTextureIndex > 2)
            mTextureIndex = 0;

        switch(mTextureIndex)
        {
            case 0:
                mMesh.getAppearance(0).setTexture(0, mTexture1);
                mMesh.getAppearance(0).getTexture(0).setBlending(Texture2D.FUNC_REPLACE);
                break;
            case 1:
                mMesh.getAppearance(0).setTexture(0, mTexture2);                
                mMesh.getAppearance(0).getTexture(0).setBlending(Texture2D.FUNC_REPLACE);
                break;
            case 2:
                mMesh.getAppearance(0).setTexture(0, mTexture3);
                mMesh.getAppearance(0).getTexture(0).setBlending(Texture2D.FUNC_REPLACE);
                break;        
            default:
                System.out.println("Error switching texture");
        }
    }
    
    // This is used to rotate the ship at a smooth constant 
    // rate. In our game we use it to show-off our hover model
    // during selection 
    public void rotate()
    {
        mTransform.postRotate(5.0f, 0, 1, 0);
    }
    
    // This is used during collision detection.  If we hit a 
    // wall we turn them 10 degrees so they keep moving forward.
    // This is important because if the player is facing the wrong 
    // direction he can hold down FIRE and eventually it'll turn 
    // around enough until it can move forward.
    public void rotate(float angle, float x, float y, float z)
    {
        mTransform.postRotate(angle, x, y, z);
    }
    
    // This moves the racer forward based on the speed
    public void update()
    {
        mTransform.postTranslate(0, 0, mSpeed);
    }
    
    // Draw the racer
    public void draw(Graphics3D g3D)
    {
        g3D.render(mMesh, mTransform);
    }
    
    // These movements are used during the placement
    // of the hover ship on the starting line.
    public void moveForward()
    {
        mTransform.postTranslate(0, 0, 0.1f);
    }
    
    public void moveBackward()
    {
        mTransform.postTranslate(0, 0, -0.06f);
    }
    
    // Here we turn the craft to the left and right
    // but notice we use a large amount of 10 degrees
    // This addes a new challenge.  It is very difficult
    // and takes a bit of luck to get your racer to go 
    // completely straight.
    public void turnLeft()
    {
        mTransform.postRotate(10, 0, 1, 0);
    }
    
    public void turnRight()
    {
        mTransform.postRotate(-10, 0, 1, 0);
    }
    
    // Set the position
    public void setPosition(float x, float y, float z)
    {
        float[] matrix = new float[16];
        mTransform.get(matrix);
        matrix[3] = x;
        matrix[7] = y;
        matrix[11] = z;
        mTransform.set(matrix);
    }
    
    public float[] getPosition()
    {
        float[] matrix = new float[16];
        float[] xyz = new float[3];
        mTransform.get(matrix);
        xyz[0] = matrix[3];
        xyz[1] = matrix[7];
        xyz[2] = matrix[11];
        return xyz;
    }
    
    public Transform getTransform()
    {
        return mTransform;
    }
    
    // Get the speed of the hover ship
    public float getSpeed()
    {
        return(mSpeed);
    }
}
