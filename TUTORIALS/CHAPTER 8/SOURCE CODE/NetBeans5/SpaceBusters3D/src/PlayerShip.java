import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.game.*;
import javax.microedition.m3g.Mesh;

public class PlayerShip
{
    private int mLife;
    private Mesh mMesh;
    
    /** Creates a new instance of SpaceEnemy */
    public PlayerShip(Mesh baseMesh, float x, float y, float z, int life)
    {
        mMesh = baseMesh;
        mMesh.setTranslation(x, y, z);
        mLife = life;
    }
    
    public void SetLife(int amount)
    {
        mLife = amount;
    }
    
    public boolean IsAlive()
    {
        if (mLife <= 0)
            return false;
        else
            return true;
    }
    
    public void MoveHoriz(float amount)
    {
        mMesh.translate(amount, 0, 0);
    }
    
    public void MoveVert(float amount)
    {
        mMesh.translate(0, 0, amount);
    }
    
    public float[] GetTranslation()
    {
        float[] xyz = new float[3];
        mMesh.getTranslation(xyz);
        return xyz;
    }
    
    public void SetTranslation(float x, float y, float z)
    {
        mMesh.setTranslation(x, y, z);
    }
    
    public void SetPickable(boolean value)
    {
        mMesh.setPickingEnable(value);
    }
    
    public Mesh GetMesh()
    {
        return mMesh;
    }
}
