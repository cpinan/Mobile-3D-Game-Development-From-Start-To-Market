import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; 
import javax.microedition.lcdui.Graphics; 
import javax.microedition.lcdui.game.GameCanvas; 
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.World;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.MorphingMesh;

public class MorphingMeshCanvas extends GameCanvas implements Runnable 
{
    // Declare our screen size so we can reference it throughout the game
    public static int SCREEN_WIDTH  = 240;
    public static int SCREEN_HEIGHT = 320;

    private World mWorld;
    private Graphics3D mG3D;
    private Camera mCam;

    private Display mDisplay; // hold the display object from the MIDlet
    private Font mGameFont; // hold the font that we will use for our game

    private MorphingMesh mEnemy;
    private int mEnemyTimer;
    
    // This constructor recieves the display
    //  so we can set it later in our thread
    public MorphingMeshCanvas(Display d) 
    {
        super(true); // call base class constructor
        mDisplay = d; // store display object
    }

    // This function will initialize our game and game objects
    private void GameInitialize()
    {      
        // set to fullscreen to use the entire 
        //  display or false to only use
        //  the area inbetween the two bars on the display
        setFullScreenMode(true);

        // setup 3D
        try
        {
            Object3D[] buffer = Loader.load("/Bee.m3g");

            for(int i = 0; i < buffer.length; i++)
            {
                if(buffer[i] instanceof World)
                {
                    // get our world node
                    mWorld = (World)buffer[i];
                    mEnemy = (MorphingMesh)mWorld.find(5);
                    mEnemyTimer = 0;
                    break;
                }
            }
        }
        catch(Exception e) 
        { System.out.println("Loading error!" + e.getMessage()); }

        // create a game font
        mGameFont = Font.getFont(Font.FACE_MONOSPACE, 
                   Font.STYLE_PLAIN, Font.SIZE_SMALL);

        // Get the active camera from the world
        mCam = mWorld.getActiveCamera();
        mCam.translate(0, 1.0f, 0);

        // Create an ambient light and add it to the world node
        Light light = new Light();
        light.setMode(Light.AMBIENT);
        light.setIntensity(3);
        mWorld.addChild(light);
    }

    // this function will be used to 
    //  update our game each frame.  All logic
    //  and animation updates can go here
    private void GameUpdate() 
    {        
        mEnemy.preRotate(2.0f, 0, 1, 0);
        mEnemy.animate(mEnemyTimer += 50);
    }

    // this is the rendering function in 
    //  our Canvas.  It is sent the Graphics
    //  object so we can use it to display our game objects
    private void GameDraw(Graphics g) 
    {
        // clear the display
        g.setColor(0x000000); // black
        g.fillRect(0, 0, getWidth(), getHeight()); 

        // drawing code goes here
        g.setFont(mGameFont);
        g.setColor(255, 255, 255);

        try
        {
            mG3D = Graphics3D.getInstance();

            // First bind the graphics object. 
            //  We use our pre-defined rendering hints.
            mG3D.bindTarget(g, true, 
                                Graphics3D.ANTIALIAS | 
                                Graphics3D.TRUE_COLOR |
                                Graphics3D.DITHER);

            mG3D.render(mWorld);
        }
        catch(Exception e)
        {
            System.err.println("Error in render loop ");
        }
        finally
        {
           
            mG3D.releaseTarget();
        }

        // end of drawing code

        // Flush the offscreen graphics buffer
        flushGraphics();
    }

    // This is called in the MIDlet and gets our GameCanvas started
    public void Start() 
    {
        // Set the canvas as the current phone's screen
        mDisplay.setCurrent(this);

        // we call our own initialize function to setup all game objects
        GameInitialize();

        // Here we setup the thread and start it
        Thread thread = new Thread(this);
        thread.start();
    }

    // This is called by the thread
    public void run() 
    {
        // get the instance of the graphics object
        Graphics g = getGraphics();

        // The main game loop
        while(true) 
        {
            GameUpdate(); // update game
            GameDraw(g); // draw game
        }   
    }


} 
