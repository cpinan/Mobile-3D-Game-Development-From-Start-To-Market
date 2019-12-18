/*
 * GameCamera.java
 *
 * Created on October 22, 2006, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

public class GameCamera extends Camera
{
    private double mCameraRot;
    private float mMoveSpeed;
    private Transform mTransform;
    float[] mMatrix = new float[16];
    
    public GameCamera()
    {
        super();
        
        // Create a cam with perspective of: 60deg view angle, aspect ratio of 1
        //  near plane at 0.03f and far at 25.0f
        this.setPerspective(60.0f, (float)320/(float)240, 0.1f, 100.0f);
        
        // set the transform
        mTransform = new Transform();
        mTransform.postTranslate(12.18f, 0.0f, -7.68f);
        SavageGameCanvas.worldGroup.addChild(this);
        mMoveSpeed = 0.8f;
    }
    
    public void reset()
    {
        mTransform.setIdentity();
    }
    
    public void moveForward()
    {
        mTransform.postTranslate(0, 0, -1*mMoveSpeed);
    }
    
    public void moveBackward()
    {
        mTransform.postTranslate(0, 0, mMoveSpeed);
    }
    
    public void moveUp()
    {
        mTransform.postTranslate(0, 0.1f, 0);
    }
    
    public void moveDown()
    {
        mTransform.postTranslate(0, -0.1f, 0);
    }
    
    public void moveLeft()
    {
        mTransform.postTranslate(-1*mMoveSpeed, 0, 0);
    }
    
    public void moveRight()
    {
        mTransform.postTranslate(mMoveSpeed, 0, 0);
    }
    
    public void lookLeft()
    {
        mTransform.postRotate(5, 0, 1.0f, 0);
    }
    
    public void lookRight()
    {
        mTransform.postRotate(-5, 0, 1.0f, 0);
    }
    
    public Transform getTransform()
    {
        return mTransform;
    }
    
    public float[] getPosition()
    {
        float[] xyz = new float[3];
        mTransform.get(mMatrix);
        xyz[0] = mMatrix[3];
        xyz[1] = mMatrix[7];
        xyz[2] = mMatrix[11];
        return xyz;
    }
    
    public float[] getMatrix()
    {
        mTransform.get(mMatrix);
        return mMatrix;
    }
}
