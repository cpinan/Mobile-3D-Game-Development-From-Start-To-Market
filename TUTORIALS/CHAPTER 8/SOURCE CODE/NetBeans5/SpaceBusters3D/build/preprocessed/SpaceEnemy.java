import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Transform;

public class SpaceEnemy
{
    public float mSpeed;
    private int mLife;
    private int mMoveDir;
    private boolean mAlive;
    private Mesh mMesh;
    private float mX;
    private float mY;
    private float mZ;
    
    /** Creates a new instance of SpaceEnemy */
    public SpaceEnemy(Mesh mesh, float x, float y, float z, int life)
    {
        mMesh = mesh;
        
        mX = x;
        mY = y;
        mZ = z;
        
        mSpeed = 0.8f;
        mMoveDir = 1;
        mLife = life;
        mAlive = true;
        
        // make sure our mesh is set to X, Y, Z
        mMesh.setTranslation(mX, mY, mZ);
    }
    
    public void Update()
    {
        // update to keep the enemies in sync
        mMesh.translate(mMoveDir*mSpeed, 0, 0);
    }
    
    public void Draw(Graphics3D g3D)
    {
        Transform transform = new Transform();
        mMesh.getTransform(transform);
        g3D.render(mMesh, transform);
    }
    
    public void SetMoveLeft()
    {
        mMoveDir = -1;
    }
    
    public void SetMoveRight()
    {
        mMoveDir = 1;
    }
    
    public void MoveDown()
    {
        mMesh.translate(0, 0, 3.6f);
    }
    
    public void DecreaseLife(int amount)
    {
        mLife -= amount;
    }
    
    public int GetLife()
    {
        return mLife;
    }
    
    public boolean IsAlive()
    {
        return mAlive;
    }
    
    public void Kill()
    {
        mAlive = false;
        mMesh.setRenderingEnable(false);
    }
    
    public float GetX()
    {
        float[] xyz = new float[3];
        mMesh.getTranslation(xyz);
        return xyz[0];
    }
    
    public float GetY()
    {
        float[] xyz = new float[3];
        mMesh.getTranslation(xyz);
        return xyz[1];
    }
    
    public float GetZ()
    {
        float[] xyz = new float[3];
        mMesh.getTranslation(xyz);
        return xyz[2];
    }
    
    public float[] GetTranslation()
    {
        float[] xyz = new float[3];
        mMesh.getTranslation(xyz);
        return xyz;
    }
    
    public Mesh GetMesh()
    {
        return mMesh;
    }
}
