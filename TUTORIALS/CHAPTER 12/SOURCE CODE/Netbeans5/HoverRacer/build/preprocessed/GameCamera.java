/*
 * GameCamera.java
 *
 * Created on October 22, 2006, 4:01 PM
 *
 */
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

public class GameCamera extends Camera
{
    private double mCameraRot;
    private float mMoveSpeed;
    private Transform mTransform;
    float[] matrix = new float[16];
    
    public GameCamera() 
    {
        super();

        // Create a cam with perspective of: 60deg view angle, aspect ratio of 1
        //  near plane at 0.03f and far at 25.0f
        this.setPerspective(60.0f, (float)320/(float)240, 0.1f, 100.0f);
        
        // set the transform
        mTransform = new Transform();
        mMoveSpeed = 0.8f;
    }
 
    // Set our transform back to an identity matrix
    // therefor resetting the matrix back to its
    // starting state
    public void reset()
    {
        mTransform.setIdentity();
    }
    
    // These are all our basic camera movements that 
    // are used in all types of games as well as 
    // traversing our game world during development
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
    
    public void lookDown(float amount)
    {
        mTransform.postRotate(-amount, 1, 0, 0);
    }

    public void lookUp(float amount)
    {
        mTransform.postRotate(amount, 1, 0, 0);
    }
    
    public Transform getTransform()
    {
        return mTransform;
    }
    
    // Set the cameras postion using its transform matrix
    public void setPosition(float x, float y, float z)
    {
        mTransform.get(matrix);
        matrix[3] = x;
        matrix[7] = y;
        matrix[11] = z;
        mTransform.set(matrix);
    }
    
    // get the position of the camera
    public float[] getPosition()
    {
        float[] xyz = new float[3];
        mTransform.get(matrix);
        xyz[0] = matrix[3];
        xyz[1] = matrix[7];
        xyz[2] = matrix[11];
        return xyz;
    }
    
    public float[] getMatrix()
    {
        mTransform.get(matrix);
        return matrix;
    }
    
    // This makes our camera look straight down
    // and assumes the camera was looking down the
    // -Z axis
    public void lookAtGround()
    {
        mTransform.postRotate(-90, 1, 0, 0);
    }
    
    // rotate the camera user defined amount
    public void rotate(float amount, float x, float y, float z)
    {
        mTransform.postRotate(amount, x, y, z);
    }
    
    // This is the first truly new function of our GameCamera
    // class.  As the name implies, this will make the camera
    // look at the x, y, z point of our choice.  The math
    // is similar to how we oriented our raptor (Chapter 9)
    // towards the player.  
    public void lookAt(float x, float y, float z) 
    { 
        // Create a vector to use in calcuations
        float lookAt[] = new float[3];
        lookAt[0] = x;
        lookAt[1] = y;
        lookAt[2] = z;
        
        // get our position and a basic up vector
        float[] pos = getPosition();
        float[] up = {0, 1, 0};
        
        // we get our transform matrix because we want
        // all our calculations to effect the camera transform
        // but we will not move the cameras position.  We only
        // want it to pivot around
	float matrix[]=new float[16];
        mTransform.get(matrix);
        
        // basic right up and direction vectors
	float vectorRight[] = {1.0f, 0.0f, 0.0f};
	float vectorUp[] = {0.0f, 1.0f, 0.0f};
	float vectorDirection[] = {0.0f, 0.0f, 0.0f};
	
        // calculate the diection vector from our position
        // to the point we're looking at
        vectorDirection[0]= lookAt[0] - pos[0]; 
        vectorDirection[1] = lookAt[1] - pos[1]; 
        vectorDirection[2] = lookAt[2] - pos[2]; ; 

        // just like before keep our vectors normalized
        vectorNormalize(vectorDirection); 
  
	// we take the cross product of the direction and up
        // vectors to get the right vector
        vectorCrossProduct(vectorDirection, up, vectorRight); 
        vectorNormalize(vectorRight); 
 
        // make sure our axis are never aligned!  
        if (vectorRight.equals(vectorDirection))
	    vectorRight[0] += 0.3f;
	
        // now calculate our new up vector (up will be different when we 
        // rotate about our position)
        vectorCrossProduct(vectorRight, vectorDirection, vectorUp); 
	                                            
        // set up our marix's rotation and direction vectors
        // so that we are now looking at the object.  Also
        // notice that matrix[3], 7, and 11 have been removed.
        // This is because we don't want to change the cameras 
        // posiiton.
        matrix[0]=vectorRight[0]; 
	matrix[1]=vectorUp[0]; 
	matrix[2]=-vectorDirection[0]; 
        matrix[4]=vectorRight[1]; 
        matrix[5]=vectorUp[1]; 
        matrix[6]=-vectorDirection[1]; 
        matrix[8]=vectorRight[2]; 
        matrix[9]=vectorUp[2]; 
        matrix[10]=-vectorDirection[2]; 
 
        // now set our changes to the matricies transform
        mTransform.set(matrix); // Set the matrix of the transform. 
    } 
  
    // normalize by 'dividing' by length (Really we do a
    // multiply because its faster but the ideas the same)
    private static float vectorNormalize(float vector[]) 
    { 
       
        // find the length of the vector given to us
        float vectorLength =(float)Math.sqrt((vector[0]*vector[0])
        + (vector[1]*vector[1]) + (vector[2]*vector[2])); 
 
        // make sure we're not already normalized
        if(vectorLength > 0.0f) 
	{ 
            // here we will multiply because it is faster than dividing
            float divisor = 1 / vectorLength; 
            vector[0] *= divisor; 
            vector[1] *= divisor; 
            vector[2] *= divisor; 
        } 
	return vectorLength;  // return our normalized vector's length
    } 
 
    // We calculate the cross product here.  We typically use 
    // this to get a new vector of an object by crossing two 
    // vectors we alreay know.  (Such as the up and look, in 
    // order to obtain the right vector)
    private static void vectorCrossProduct(float vector1[], float vector2[], float vectorOutput[]) 
    { 
        if(vectorOutput == vector1 || vectorOutput == vector2) 
	{ 
            // calc our cross prodcut
	    float x = (vector1[1]*vector2[2]) - (vector1[2]*vector2[1]); 
            float y = (vector1[2]*vector2[0]) - (vector1[0]*vector2[2]); 
 
            // Now set our values
            vectorOutput[0]=y; 
            vectorOutput[1]=x; 
            vectorOutput[2]=(vector1[0] * vector2[1]) - (vector1[1] * vector2[0]); 
        
        } 
        else 
	{ 
            // set the calculated crossproduct values
	    vectorOutput[0]=(vector1[1]*vector2[2])-(vector1[2]*vector2[1]); 
            vectorOutput[1]=(vector1[2]*vector2[0])-(vector1[0]*vector2[2]); 
            vectorOutput[2]=(vector1[0]*vector2[1])-(vector1[1]*vector2[0]); 
        } 
    } 
   
}
