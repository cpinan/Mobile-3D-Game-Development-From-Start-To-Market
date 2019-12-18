//import required packages
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import javax.microedition.m3g.*;

public class RetainedCanvas extends Canvas 
{
    private Display mDisplay;
    
    //declare the World
    private World mMyWorld = null;
    
    //declare the Graphics3D renderer
    private Graphics3D mG3D = null;
    private Background mBackground;
    
    public RetainedCanvas(Display d) 
    {
        super();
        mDisplay = d;
    }
    
    public void start() throws MIDletStateChangeException 
    {
        mDisplay.setCurrent(this);
        try 
        {
            //use Loader to import the ship file
            Object3D[] importedm3g = Loader.load("/ship.m3g");
            for(int i = 0; i < importedm3g.length; i++) 
            {
                if(importedm3g[i] instanceof World) 
                {
                    // load our world and then our ships
                    mMyWorld= (World)importedm3g[i];
                    break;
                }
            }
            
            //get an instance of the renderer
            mG3D=Graphics3D.getInstance();
            
            mBackground = new Background();
            
            repaint();
        } catch (Exception e) 
        { System.out.println("Error with start!"); }
    }
    
    public void paint(Graphics g) 
    {
        if(mMyWorld == null) return;
        
        //bind a target, render, and release
        mG3D.bindTarget(g);
        mG3D.clear(mBackground);
        mG3D.render(mMyWorld);
        mG3D.releaseTarget();
    }
}