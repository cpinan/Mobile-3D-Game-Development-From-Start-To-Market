//import required classes
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.World;

//Declare class
public class IModeRenderingCanvas extends GameCanvas implements Runnable 
{
    private Display mDisplay;
    
    private Graphics3D mGraphics3D;
    private Background mBackground;
    private Camera mCam;
    private Light mLight;
    private Mesh mShip;
    private Transform mCamTrans;
    private Transform mShipTrans;
    private Transform mShip2Trans;
    private Transform mLightTrans;
    
    public IModeRenderingCanvas(Display d) 
    {
        super(true);
        mDisplay = d;
    }
    
    // This function will initialize our game and game objects
    private void gameInitialize() 
    {
        try 
        {
            //create instance of Graphics3D renderer
            mGraphics3D = Graphics3D.getInstance();
            
            // load and extract ship from the world
            Object3D[] importedm3g = Loader.load("/ship.m3g");
            for(int i = 0; i < importedm3g.length; i++) 
            {
                if(importedm3g[i] instanceof World) 
                {
                    // load our world and then our ships
                    World worldObject = (World)importedm3g[i];
                    
                    mShip = (Mesh)worldObject.find(5);
                    break;
                }
            }
        } catch(Exception e) 
        {
            System.out.println("There is a loading error");
        }
        
        //create a new camera
        mCam = new Camera();
        mCam.setPerspective(35.0f, (float)getWidth()/ (float)getHeight(), 0.1f, 1000.0f);
        
        //create a while mBackground
        mBackground = new Background();
        mBackground.setColor(0xFFFFFF);
        
        //create a new directional light
        mLight = new Light();
        mLight.setMode(Light.DIRECTIONAL);
        mLight.setColor(0xFDFEB4);
        mLight.setIntensity(2.0f);
        
        //transform for light
        mLightTrans = new Transform();
        mLightTrans.setIdentity();
        mLightTrans.postRotate(-45f, 0, 1, 0);
        
        //transform for camera
        mCamTrans = new Transform();
        mCamTrans.setIdentity();
        mCamTrans.postTranslate(0, 0, 3.0f);
        
        //transform for ship1
        mShipTrans = new Transform();
        mShipTrans.postTranslate(0.5f, 0, 0);
        
        //transform for ship2
        mShip2Trans = new Transform();
        mShip2Trans.postTranslate(-0.5f, 0, 0);
        
        //add the light to the graphics3D object
        mGraphics3D.addLight(mLight, mLightTrans);
        
        //add camera to the graphics3D object
        mGraphics3D.setCamera(mCam, mCamTrans);
    }
    
    
    //Logic and animation
    private void gameUpdate() 
    {
        //rotate our ships
        mShipTrans.postRotate(-10.0f, 0, 1, 0);
        mShip2Trans.postRotate(10.0f, 0, 1, 0);
    }
    
    private void gameDraw(Graphics g) 
    {
        // drawing code goes here
        try 
        {
            // First bind the graphics object
            mGraphics3D.bindTarget(g, true, 0);
            
            mGraphics3D.clear(mBackground);
            
            //render ships
            mGraphics3D.render(mShip, mShipTrans);
            mGraphics3D.render(mShip, mShip2Trans);
        } catch(Exception e) 
        { 
            System.err.println("Rendering problem"); 
        } 
        finally 
        {
            mGraphics3D.releaseTarget();
        }
        
        flushGraphics();
    }
    
    public void start() 
    {
        // Make the canvas the screen of the phone
        mDisplay.setCurrent(this);
        
        // Initialize our game
        gameInitialize();
        
        //Create thread and start it
        Thread thread = new Thread(this);
        thread.start();
    }
    
    //Entry point for thread
    public void run() 
    {
        // graphics object
        Graphics g = getGraphics();
        
        // The main game loop
        while(true) 
        {
            gameUpdate();
            gameDraw(g);
            
            //Wait period at end of execution loop
            try 
            {
                Thread.sleep(30); 
            } catch(Exception e) { System.out.println("Error in run loop"); }
        }
    }
}