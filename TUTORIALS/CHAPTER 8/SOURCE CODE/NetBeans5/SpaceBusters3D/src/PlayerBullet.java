import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.m3g.Mesh;

public class PlayerBullet
{
    public boolean mAlive;
    private int mDamage;
    private float mSpeed;
    private float[] xyz;
    private Mesh mMesh;
    
    /** Creates a new instance of PlayerBullet */
    public PlayerBullet(Mesh baseMesh, int damage)
    {
        mDamage = damage;
        mAlive = false;
        mSpeed = -2.0f;
        xyz = new float[3];
        mMesh = baseMesh;
        mMesh.setRenderingEnable(false);
        mMesh.setTranslation(-50, 0, 0);
    }
    
    public void Update()
    {
        if (mAlive)
        {
            mMesh.translate(0, 0, mSpeed);
            mMesh.getTranslation(xyz);
            
            if (xyz[2] < -32.0f)
            {
                mAlive = false;
                mMesh.setRenderingEnable(false);
            }
        }
    }
    
    public void SetPosition(float x, float y, float z)
    {
        mMesh.setTranslation(x, y, z);
    }
    
    public float[] GetTranslation()
    {
        float[] xyz = new float[3];
        mMesh.getTranslation(xyz);
        return(xyz);
    }
    
    
    public void EnableRender(boolean value)
    {
        mMesh.setRenderingEnable(value);
    }
}
