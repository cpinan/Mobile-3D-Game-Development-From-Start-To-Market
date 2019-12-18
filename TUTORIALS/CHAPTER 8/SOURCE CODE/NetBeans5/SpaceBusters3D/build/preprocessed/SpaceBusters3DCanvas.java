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

public class SpaceBusters3DCanvas extends GameCanvas implements Runnable
{
    // Declare our screen size so we can reference it throughout the game
    public static final int SCREEN_WIDTH  = 240;
    public static final int SCREEN_HEIGHT = 320;
    
    public static boolean mGameOverWin = false;
    public static boolean mGameOver = false;
    
    private World mWorld;
    private Graphics3D mG3D;
    private Camera mCam;
    
    private Display mDisplay; // hold the display object from the MIDlet
    private Font mGameFont; // hold the font that we will use for our game
    
    private EnemyManager mEnemyManager;
    
    private PlayerShip mPlayerShip;
    
    private SpaceEnemy[] mSpaceEnemies;
    private PlayerBullet[] mBullets;
    
    private float[] mXYZ = new float[3];
    private float mPlayerX = 0;
    private float mPlayerY = 0;
    private float mPlayerZ = 0;
    
    // This constructor recieves the display
    //  so we can set it later in our thread
    public SpaceBusters3DCanvas(Display d)
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
        mSpaceEnemies = new SpaceEnemy[9];
        mBullets = new PlayerBullet[3];
        
        try
        {
            Object3D[] buffer = Loader.load("/SBFinal.m3g");
            
            for(int i = 0; i < buffer.length; i++)
            {
                if(buffer[i] instanceof World)
                {
                    // get our world node
                    mWorld = (World)buffer[i];
                    
                    // Top Row Enemies:
                    mSpaceEnemies[0] = new
                            SpaceEnemy((Mesh)mWorld.find(131),
                            -11.0f, 0.0f, -32.0f, 100);
                    mSpaceEnemies[1] = new
                            SpaceEnemy((Mesh)mWorld.find(139),
                            -7.0f, 0.0f, -32.0f, 100);
                    mSpaceEnemies[2] = new
                            SpaceEnemy((Mesh)mWorld.find(147),
                            -3.0f, 0.0f, -32.0f, 100);
                    
                    // Middle row enemies
                    mSpaceEnemies[3] = new
                            SpaceEnemy((Mesh)mWorld.find(123),
                            -11.0f, 0.0f, -28.0f, 100);
                    mSpaceEnemies[4] = new
                            SpaceEnemy((Mesh)mWorld.find(115),
                            -7.0f, 0.0f, -28.0f, 100);
                    mSpaceEnemies[5] = new
                            SpaceEnemy((Mesh)mWorld.find(107),
                            -3.0f, 0.0f, -28.0f, 100);
                    
                    // Bottom Row Enemies
                    mSpaceEnemies[6] = new
                            SpaceEnemy((Mesh)mWorld.find(99),
                            -11.0f, 0.0f, -24.0f, 100);
                    mSpaceEnemies[7] = new
                            SpaceEnemy((Mesh)mWorld.find(66),
                            -7.0f, 0.0f, -24.0f, 100);
                    mSpaceEnemies[8] = new
                            SpaceEnemy((Mesh)mWorld.find(91),
                            -3.0f, 0.0f, -24.0f, 100);
                    
                    // Player Ship
                    mPlayerShip = new PlayerShip((Mesh)mWorld.find(5),
                            0f, 0f, 7.0f, 100);
                    
                    // Player Bullets
                    mBullets[0] = new PlayerBullet(
                            (Mesh)mWorld.find(164), 20);
                    mBullets[1] = new PlayerBullet(
                            (Mesh)mWorld.find(155), 20);
                    mBullets[2] = new PlayerBullet(
                            (Mesh)mWorld.find(169), 20);
                    break;
                }
            }
        }
        catch(Exception e)
        { System.out.println("Loading error!" + e.getMessage()); }
        
        // create a game font
        mGameFont = Font.getFont(Font.FACE_MONOSPACE,
                Font.STYLE_PLAIN, Font.SIZE_SMALL);
        
        mEnemyManager = new EnemyManager(mSpaceEnemies);
        PlayerAttackManager.Initialize(mBullets);
        
        // Get the active camera from the world
        mCam = mWorld.getActiveCamera();
        
        // Create an ambient light
        Light light = new Light();
        light.setMode(Light.AMBIENT);
        light.setIntensity(3);
        
        // Add to the world node
        mWorld.addChild(light);
    }
    
    // this function will be used to
    //  update our game each frame.  All logic
    //  and animation updates can go here
    private void GameUpdate()
    {
        if (this.mGameOver == false && this.mGameOverWin == false)
        {
            CheckInput();
            mEnemyManager.Update();
            PlayerAttackManager.Update();
            // Check Collisions
            CheckPlayer();
            PlayerAttackManager.CheckBulletsEnemies(
                    mEnemyManager.GetEnemies());
        }
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
        
        if (this.mGameOver == true)
        {
            g.setColor(255, 0, 0);
            g.drawString("Game Over", SCREEN_WIDTH/2,
                    SCREEN_HEIGHT/2, Graphics.TOP|Graphics.HCENTER);
        }
        else if(this.mGameOverWin == true)
        {
            g.setColor(0, 255, 0);
            g.drawString("You Win!", SCREEN_WIDTH/2,
                    SCREEN_HEIGHT/2, Graphics.TOP|Graphics.HCENTER);
        }
        else
        {
            try
            {
                mG3D = Graphics3D.getInstance();
                
                // First bind the graphics object.
                //  We use our pre-defined rendering hints.
                mG3D.bindTarget(g, true,
                        Graphics3D.ANTIALIAS |
                        Graphics3D.TRUE_COLOR |
                        Graphics3D.DITHER);
                
                // Render
                mG3D.render(mWorld);
            }
            catch(Exception e)
            {
                System.err.println("Error in render loop " + e.getMessage());
            }
            finally
            {
                // Always remember to release!
                mG3D.releaseTarget();
            }
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
    
    private void CheckInput()
    {
        int keyState = getKeyStates();
        if ((keyState & FIRE_PRESSED) != 0)
        {
            float[] mXYZ = mPlayerShip.GetTranslation();
            PlayerAttackManager.FireBullet(mXYZ[0], mXYZ[1], mXYZ[2]);
        }
        if ((keyState & UP_PRESSED) != 0)
        {
            mPlayerShip.MoveVert(-1f);
            mXYZ = mPlayerShip.GetTranslation();
            if (mXYZ[2] < -32)
                mPlayerShip.SetTranslation(mXYZ[0], mXYZ[1], -32);
        }
        if ((keyState & DOWN_PRESSED) != 0)
        {
            mPlayerShip.MoveVert(1f);
            mXYZ = mPlayerShip.GetTranslation();
            if (mXYZ[2] > 7)
                mPlayerShip.SetTranslation(mXYZ[0], mXYZ[1], 7);
        }
        if ((keyState & LEFT_PRESSED) != 0)
        {
            mPlayerShip.MoveHoriz(-1f);
            mXYZ = mPlayerShip.GetTranslation();
            if (mXYZ[0] < -11)
                mPlayerShip.SetTranslation(-11, mXYZ[1], mXYZ[2]);
        }
        if ((keyState & RIGHT_PRESSED) != 0)
        {
            mPlayerShip.MoveHoriz(1f);
            mXYZ = mPlayerShip.GetTranslation();
            if (mXYZ[0] > 11)
                mPlayerShip.SetTranslation(11, mXYZ[1], mXYZ[2]);
        }
    }
    
    // check player ship against all other enemies
    public void CheckPlayer()
    {
        SpaceEnemy[] enemies = mEnemyManager.GetEnemies();
        float[] mXYZ = mPlayerShip.GetTranslation();
        float mPlayerX = mXYZ[0];
        float mPlayerY = mXYZ[1];
        float mPlayerZ = mXYZ[2];
        double distance = 0.0f;
        
        for (int i = 0; i < enemies.length; i++)
        {
            if (enemies[i].IsAlive())
            {
                mXYZ = enemies[i].GetTranslation();
                
                // check distance between two objects
                distance = Math.sqrt( ((mXYZ[0]-mPlayerX)
                * (mXYZ[0]-mPlayerX))
                + ((mXYZ[1]-mPlayerY)
                * (mXYZ[1]-mPlayerY))
                + ((mXYZ[2]-mPlayerZ)
                * (mXYZ[2]-mPlayerZ)) );
                
                if (distance <= 2.0f)
                {
                    SpaceBusters3DCanvas.mGameOver = true;
                    break;
                }
            }
        }
    }
} // end of class
