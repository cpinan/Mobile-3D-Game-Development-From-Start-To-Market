/*
 * FPSGameCanvas.java
 *
 * Created on Oct. 22, 2006, 10:28 PM
 *
 */
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*; 
import javax.microedition.lcdui.Graphics; 
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.World;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.RayIntersection;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Group;

// Here we declare our class and say that it extends the GameCanvas and
//  implements Runnable.  Whe are using Runnable here so that we can do all
//  our animation in a seperate thread.
public class SavageGameCanvas extends GameCanvas implements Runnable 
{
    // Declare our screen size so we can reference it throughout the game
    public static final int SCREEN_WIDTH  = 240;
    public static final int SCREEN_HEIGHT = 320;
    
    // These are our games possible states
    public static boolean GAME_OVER = false;
    public static boolean GAME_WON = false;
    public boolean mIsClean = false;
    
    // Drawing objects and background to clear with
    private static Display mDisplay;
    private Graphics3D mG3D;
    private Background background;

    // Splash Screen And Menu System
    private int mRenderState = -1;
    private static int RENDERSTATE_STARTMENU = 1;
    private static int RENDERSTATE_GAME = 2;
    private Image splashScreen;
    private Sprite gameStartOptions;
    
    // game over and game won screens and menus
    private Image gameOverScreen;
    private Image gameWonScreen;
    private Sprite gameOptions;
    
    // our own worldGroup for collision detection
    public static Group worldGroup;
    private RayIntersection rayIntersect = new RayIntersection();
    
    // This is a debug state that allows us to control
    //  if we want to collide or not.
    //  False - we walk aorund w/ out collision detection
    public static boolean checkCollide = true; 

    // The rooms in our game
    private Room room1;
    private Room room2;
    private Room room3;
    private Room room4;
    
    // The doors in our game are simply mesh
    //  objects that block our view from
    //  seeing unrendered objects
    private Mesh door1;
    private Mesh door2;
    private Mesh door3;
    
    // The transform for our doors
    private Transform doorsTrans;
    
    // Our game's camera which
    // represents the player 
    private GameCamera gameCam;
    
    // We will use XYZ and matrix to 
    // retrieve our players position.  
    // We make it global because we'll 
    // access them so often
    private float[] xyz = new float[3];    
    private float[] matrix = new float[16];

    // Our games entities that hold
    //  health, weapons, and control
    //  enemies.  
    private HealthEntity healthEntity;
    private WeaponEntity gunEntity;
    private NestEntity nestEntity;

    // time variables so we can delay our input 
    //  keypresses.
    private long mLastFiredTime = 0;
    private long mCurrentTime = 0;
    private long mLastKeyPress = 0;
    private int mKeyPressDelay = 500;
    private int mFireDelay = 100;
    

    // This constructor recieves the display so we can set it later in our thread
    public SavageGameCanvas(Display d) 
    {
	super(true); // call base class constructor
	mDisplay = d; // store display object
    }
    
    // This function will initialize our game and game objects
    private void gameInitialize()
    {      
	// set to fullscreen to use the entire display or false
        // to only use the area inbetween the two bars on the 
        // display
	setFullScreenMode(true);

        // initialize our worldGroup 
        //  so we can add to it later
        worldGroup = new Group();
        
        try
        {
            // creating our menu system screens
            splashScreen = Image.createImage("/Menus/startgame/screen.png");
            gameStartOptions = new Sprite(Image.createImage("/Menus/gameOptions240x68.png"), 240, 68);
            gameStartOptions.setPosition(0, SCREEN_HEIGHT-gameStartOptions.getHeight());
            mRenderState = RENDERSTATE_STARTMENU; // set the starting render state

            // game won and game over screens
            gameOverScreen = Image.createImage("/Menus/gameover/Death.png");
            gameWonScreen = Image.createImage("/Menus/gamewon/Win.png");
            gameOptions = new Sprite(Image.createImage("/Menus/gameOptions240x68.png"), 240, 68);
            gameOptions.setPosition(0, SCREEN_HEIGHT-gameOptions.getHeight());
            
            // now we load our 3D level and break it up into room
            // objects that we can mainpulate later
            Object3D[] buffer = Loader.load("/Level/Level.m3g");
            for(int i = 0; i < buffer.length; i++)
            {
                if(buffer[i] instanceof World)
                {
                    World worldObject = (World)buffer[i];                    
                    
                    // Create the rooms and set bounding squares
                    //  on the room's x and z coordinates.  This
                    //  will allow us to determine where if the 
                    //  player is in each room.  Notice the 
                    //  over lap in some locations.  This is 
                    //  so when you walk into antoher room it 
                    //  doesn't just appear                           
                    room1 = new Room((Mesh)worldObject.find(5), 5, -10, -10, 12);
                    room2 = new Room((Mesh)worldObject.find(22), 18.6f, -15, 6.09f, -1.1f);
                    room3 = new Room((Mesh)worldObject.find(35), 28, -1.6f, 16.8f, 11);
                    room4 = new Room((Mesh)worldObject.find(48), 20, -8.5f, 3.2f, 3.5f);
                    
                    // load in the doors as mesh objects
                    door1 = (Mesh)worldObject.find(61);
                    door2 = (Mesh)worldObject.find(74);
                    door3 = (Mesh)worldObject.find(85);
                    
                    // Here we remove each object from the world
                    //  so each object no longer belongs to a parent
                    //  object.
                    worldObject.removeChild(room1.getMesh());
                    worldObject.removeChild(room2.getMesh());
                    worldObject.removeChild(room3.getMesh());
                    worldObject.removeChild(room4.getMesh());
                    worldObject.removeChild(door1);
                    worldObject.removeChild(door2);
                    worldObject.removeChild(door3);
                    
                    // Now we add our objects to our own
                    //  collision group.
                    worldGroup.addChild(room1.getMesh());
                    worldGroup.addChild(room2.getMesh());
                    worldGroup.addChild(room3.getMesh());
                    worldGroup.addChild(room4.getMesh());
                    worldGroup.addChild(door1);
                    worldGroup.addChild(door2);
                    worldGroup.addChild(door3);
                    break;
                }
            }
        }
        catch(Exception e) { System.out.println("Canvas Loading error: " + e.getMessage()); }

        // Here we get the instance to our graphics object
        mG3D = Graphics3D.getInstance();
        
        // We create our background that will be used to clear
        // the screen and we set it to black
        background = new Background();
        background.setColor(254 << 24 | 0  << 16 | 0 << 8 | 0);
                        
        // The doors use their position data
        // from the m3G file
        doorsTrans = new Transform();
        
        // when doing collision detection we don't want to pick
        //  the door objects.  This is a speed enhancement
        door1.setPickingEnable(false);
        door2.setPickingEnable(false);
        door3.setPickingEnable(false);
        
        // Lighting extremely slows down rendering so we just
        //  turn off lighting.  To do this we set each mesh's
        //  texture blending to FUNC_REPLACE and then 
        //  we do not create or add lights like we did in 
        //  the past.
        Appearance appearance = door1.getAppearance(0);
        Texture2D texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);

        appearance = door2.getAppearance(0);
        texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);

        appearance = door3.getAppearance(0);
        texture = appearance.getTexture(0);
        texture.setBlending(Texture2D.FUNC_REPLACE);
        
        // Setup our entities to initial locations and properties
        healthEntity = new HealthEntity(16.19f, -1.0f, 0.22f);
        gunEntity = new WeaponEntity(24.5f, -1.0f, 7.67f);
        nestEntity = new NestEntity(-8f, -2.5f, -9.5f, 0);
        SpearEntity.initialize(-2.24f, -1.5f, 0.422f);

        // Setup our games camera which will also
        //  act as the player
        gameCam = new GameCamera();
        
        // Setup our HUD and Frames per sec calc
        SavageHUD.Initialize();
        SavageCounter.Initialize();
    }
    
    // this function will be used to update our game each frame.  All logic
    //  and animation updates can go here
    private void gameUpdate() 
    {
        // Update the stuff we wanto always update each frame
        SavageCounter.update();
        checkInput();
        
        // now check the states and update per-state
        if (mRenderState != RENDERSTATE_STARTMENU && GAME_OVER == false && GAME_WON == false)
        {
            // here we decide what room we are in based on 
            // our x and z location and then we only draw the
            // room we are in.
            xyz = gameCam.getPosition();
            if (room1.isIn(xyz[0], xyz[2]))
            {
                // render room we're in
                room1.enableRender(true);
  
                // decide whta doors to render based on
                // what the player can see
                door1.setRenderingEnable(true);
                door2.setRenderingEnable(false);
                door3.setRenderingEnable(false);
                
                // in this specific room we need to 
                // render the nest entity and 
                // activate the enemy
                nestEntity.enableRender(true);
                nestEntity.activate();
            }
            else
            {
                // we're not in this room so don't render
                // the room or nest
                room1.enableRender(false);
                nestEntity.enableRender(false);
            }

            if (room2.isIn(xyz[0], xyz[2]))
            {
                room2.enableRender(true);
                door1.setRenderingEnable(false);
                door2.setRenderingEnable(true);
                door3.setRenderingEnable(false);
            }
            else
            {
                room2.enableRender(false);
            }

            if (room3.isIn(xyz[0], xyz[2]))
            {
                room3.enableRender(true);
                door1.setRenderingEnable(false);
                door2.setRenderingEnable(false);
                door3.setRenderingEnable(true);

                // this room contains the gun entity
                // whcih is a wepaon.  Render it.
                gunEntity.enableRender(true);
            }
            else
            {
                // disable the room and gun entity
                room3.enableRender(false);
                gunEntity.enableRender(false);
            }

            if (room4.isIn(xyz[0], xyz[2]))
            {
                room4.enableRender(true);
                door1.setRenderingEnable(true);
                door2.setRenderingEnable(true);
                door3.setRenderingEnable(true);

                // this is the hallway and has the health
                healthEntity.enableRender(true);
            }
            else
            {
                room4.enableRender(false);
                healthEntity.enableRender(false);
            }
        
            // here we update each of our enties
            // each of them are updated and inside
            // each update function they will only
            // update if they are enabled
            healthEntity.update(gameCam.getTransform());
            gunEntity.update(gameCam.getTransform());
            SpearEntity.update(gameCam.getTransform());
            nestEntity.update(gameCam.getTransform());
        }
    }
        

    // this is the rendering function in our Canvas.  It is sent the Graphics
    //  object so we can use it to display our game objects
    private void gameDraw(Graphics g) 
    {
	// drawing code goes here
        if (mRenderState == RENDERSTATE_STARTMENU)
        {
           g.drawImage(splashScreen, 0, 0, Graphics.TOP|Graphics.LEFT); 
           gameStartOptions.paint(g);
        }
        else if(GAME_OVER == true)
        {
            g.drawImage(gameOverScreen, 0, 0, Graphics.TOP|Graphics.LEFT);
            gameOptions.paint(g);
        }
        else if(GAME_WON == true)
        {
            g.drawImage(gameWonScreen, 0, 0, Graphics.TOP|Graphics.LEFT);
            gameOptions.paint(g);
        }
        else if(mRenderState == RENDERSTATE_GAME && GAME_OVER == false && GAME_WON == false)
        {
            try
            {
                // First bind the graphics object. We use our pre-defined rendering hints.
                mG3D.bindTarget(g, true, Graphics3D.ANTIALIAS|Graphics3D.TRUE_COLOR|Graphics3D.DITHER);

                // clear the surface
                mG3D.clear(background);

                // set our camera becasue we probably moved
                mG3D.setCamera(gameCam, gameCam.getTransform());

                // Render the rooms.  Only the rooms set to 
                // active in Update() will actually be drawn
                room1.draw(mG3D);
                room2.draw(mG3D);    
                room3.draw(mG3D);
                room4.draw(mG3D);
             
                // Render our doors
                mG3D.render(door1, doorsTrans);
                mG3D.render(door2, doorsTrans);
                mG3D.render(door3, doorsTrans);
                
                // Draw our entities and like 
                // our other objects.  They will 
                // only really be drawn if they were
                // set to be drawn in the Update 
                // function.
                healthEntity.draw(mG3D);
                gunEntity.draw(mG3D);
                SpearEntity.draw(mG3D);
                nestEntity.draw(mG3D);
            }
            catch(Exception e)
            {
                System.err.println("Error in render loop: " + e.getMessage());
            }
            finally
            {
                // Always remember to release!
                mG3D.releaseTarget();
            }

            // Now Draw the 2D HUD and fps counter
            SavageHUD.draw(g);
            SavageCounter.draw(g);
            
            // If we un comment the line below we can see the 
            // players position.  This is useful during development
//          g.drawString("x: " + xyz[0] + " y: " + xyz[1] 
//                             + " z: " + xyz[2], 10, 30, 
//                           Graphics.TOP|Graphics.LEFT);
           
            
        }       
        
        // end of drawing code
        flushGraphics();
    }
    
    // This is called in the MIDlet and gets our GameCanvas started
    public void start() 
    {
	// Set the canvas as the current phone's screen
	mDisplay.setCurrent(this);

        // we call our own initialize function to setup all game objects
	gameInitialize();

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
            gameUpdate(); // update game
            gameDraw(g); // draw game
            
            // In our previous applications we set this
            //  to allow for a syncing rate of 30 frames
            //  per second.  Instead we'll set it lower
            //  and use it as an adjustment for different 
            //  phones.  
            try{ Thread.sleep(5); } catch(Exception e) {}
	}   
    }

    // Check our game's input
    private void checkInput()
    {        
        int keyState = getKeyStates();
        if ((keyState & FIRE_PRESSED) != 0)
        {
            if (GAME_OVER == true || GAME_WON == true)
            {
                // Decide what to do based on 
                //  game over menu choice
                if (gameOptions.getFrame() == 1) // quit!
                    SavageGameMidlet.kill();
                else
                    this.restartGame(); // reset the game
            }
            else if (mRenderState == RENDERSTATE_STARTMENU)
            {
                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
                {
                    if (gameStartOptions.getFrame() == 1) // quit!
                        SavageGameMidlet.kill();
                    else
                    {
                        // Start the game!
                        mRenderState = RENDERSTATE_GAME;
                        splashScreen = null;
                        gameStartOptions = null;
                    }
                    
                    mLastKeyPress = mCurrentTime;
                }
            }
            else
            {
                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime > (mLastKeyPress+mFireDelay))
                {
                    if (SavageHUD.hasWeapon())
                    {
                        SavageHUD.getGun().nextFrame();

                        // shoot gun
                        matrix = gameCam.getMatrix();
                        if (worldGroup.pick(-1, matrix[3], matrix[7]+0.7f, matrix[11], -1*matrix[2], -1*matrix[6], -1*matrix[10], rayIntersect))
                        {
                            //if (rayIntersect.getIntersected().getUserID() == 56)
                            //{
                                nestEntity.hurtEnemy();
                            //}
                        }
                    }
                    
                    mLastKeyPress = mCurrentTime;
                }
            }
        } 
        if ((keyState & FIRE_PRESSED) == 0)
        {
            SavageHUD.getGun().setFrame(0);
        }
        if ((keyState & UP_PRESSED) != 0) 
        {
            if (mRenderState == RENDERSTATE_STARTMENU)
            {
                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
                {
                    gameStartOptions.nextFrame();
                    mLastKeyPress = mCurrentTime;
                }
            }
            else if(GAME_OVER == true || GAME_WON == true)
            {
                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
                {
                    gameOptions.nextFrame();
                    mLastKeyPress = mCurrentTime;
                }
            }
            else
            { 
                // Check Forwards
                matrix = gameCam.getMatrix();
                if (checkCollide && worldGroup.pick(-1, matrix[3], matrix[7], matrix[11], -1*matrix[2], -1*matrix[6], -1*matrix[10], rayIntersect))
                {
                   if (rayIntersect.getDistance() > 1.5f)
                        gameCam.moveForward();
                }
                else            
                    gameCam.moveForward();

            }
        }
        if ((keyState & DOWN_PRESSED) != 0)
        {
           if (mRenderState == RENDERSTATE_STARTMENU)
           {
               mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
                {
                    gameStartOptions.nextFrame();
                    mLastKeyPress = mCurrentTime;
                }
           }
           else if(GAME_OVER == true || GAME_WON == true)
           {
               mCurrentTime = System.currentTimeMillis();
               if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
               {
                   gameOptions.nextFrame();
                   mLastKeyPress = mCurrentTime;
               }
           }
           else
           {
                // Check Backwards
                matrix = gameCam.getMatrix();
                if (checkCollide && worldGroup.pick(-1, matrix[3], matrix[7], matrix[11], matrix[2], matrix[6], matrix[10], rayIntersect))
                {
                   if (rayIntersect.getDistance() > 1.5f)
                        gameCam.moveBackward();
                }
                else            
                    gameCam.moveBackward();
           }
        }
        if ((keyState & LEFT_PRESSED) != 0)
        {
            if (mRenderState != RENDERSTATE_STARTMENU && !GAME_OVER && !GAME_WON)
                gameCam.lookLeft(); // look 5 degrees
        }
        if ((keyState & RIGHT_PRESSED) != 0)
        {
            if (mRenderState != RENDERSTATE_STARTMENU && !GAME_OVER && !GAME_WON)
                gameCam.lookRight(); // look 5 degrees
        }
    }
        
    // This static funciton is here to expose a neat feature
    // of the Display class.  To add an added effect to our 
    // games we can have it vibrate and flash on the phone
    // when we get hit by the enemy.  This function calls
    // these functions for the duration we tell it.
    // however, this is only a request and may not work if
    // the phone does not support this feature.  
    public static void vibrateAndFlash(int duration)
    {
        // ask the phone to vibrate and flash if available
        mDisplay.vibrate(duration);
        mDisplay.flashBacklight(duration);
    }
    
    // Rest the game to the inital states by calling the 
    // gameInitialize() function and then setting the
    // currect states of the game.
    public void restartGame()
    {
        // restart all our objects so the game will start new
        gameInitialize();
        mRenderState = RENDERSTATE_GAME;
        GAME_OVER = false;
        GAME_WON = false;
    }
} // end of class