/*
 * HoverRacerCanvas.java
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
import javax.microedition.lcdui.Font;
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
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Group;

// Here we declare our class and say that it extends the GameCanvas and
//  implements Runnable.  Whe are using Runnable here so that we can do all
//  our animation in a seperate thread.
public class HoverRacerCanvas extends GameCanvas implements Runnable 
{
    // Declare our screen size so we can reference it throughout the game
    public static final int SCREEN_WIDTH  = 240;
    public static final int SCREEN_HEIGHT = 320;
    
    // Game state constants
    public static final int GAMESTATE_CONNECT = -1;
    public static final int GAMESTATE_GAMEOVER = 0;
    public static final int GAMESTATE_TITLEMENU = 1;    
    public static final int GAMESTATE_SELECTCAR = 2;
    public static final int GAMESTATE_PLACECAR = 3;
    public static final int GAMESTATE_STARTINGLINE = 4;
    public static final int GAMESTATE_RACING = 5;
    public static final int GAMESTATE_ENDRACE = 6;

    // These are our games possible states
    public static int mGameState; 
    
    // Drawing objects and mBackground to clear with
    private static Display mDisplay;
    private Graphics3D mG3D;
    private Background mBackground;
    private boolean mIsServer;
 
    // These are objects used every frame
    private float[] mRacerXYZ = new float[3];    

    // time variables so we can delay our input 
    //  keypresses.
    private long mLastFiredTime = 0;
    private long mCurrentTime = 0;
    private long mLastKeyPress = 0;
    private int mKeyPressDelay = 300;
    private int mFireKeyPressDelay = 200; 
    
    // Menus info screens for the game
    private Image mTitleScreen;
    private Sprite mGameWon;
    private Sprite mGameLost;
    private Sprite mGameOptions;
    private Sprite mGamePickShipOptions;
    private Font mGameFont;
    
    // these are our game objects
    GameCamera mGameCam;
    Mesh mTrack;
    Transform mTrackTrans;
    Racer mPlayerRacer;
    Sprite mStartingLine;
    
    // These decide if we can increase speed
    // as well as what state our camer is in
    boolean mCanIncrease;
    boolean mCam2Set;
    boolean mCam3Set;
    
    HoverNetworking mHoverNet;
    private String mStatus;
    private boolean mConnected;
    boolean mRaceTimeSent;
    boolean mRaceTimeRecieved;
    private String mMyTime;
    private String mOpponentTime;
    private long mOpponentTimeMillis;
    
    // This constructor recieves the display so we can set it later in our thread
    public HoverRacerCanvas(Display d, String type) 
    {
	super(true); // call base class constructor
	mDisplay = d; // store display object
        
        // Setup our networking by telling 
        // our networking class if we are a 
        // server or not.
        if (type.equalsIgnoreCase("Server"))
            mIsServer = true;
        else
            mIsServer = false;
                // create networking object and start listening
        // for messages
        mHoverNet = new HoverNetworking(this, mIsServer);
        mHoverNet.start(); 
        
        // holds the connection status and if we are connected
        // to the server or client
        mStatus = "";
        mConnected = false;
    }
    
    // Sets the connection status
    public void setStatus(String status)
    {
        mStatus = status;
        
        if (mStatus.equalsIgnoreCase("Connected to Client!") 
        || mStatus.equalsIgnoreCase("Connected to server!"))
            mConnected = true;
                                                                                         
    }
    
    // Recieve a message from the networking class
    // This will only be used to transmit the 
    // elapsed time of the other player from 
    // start to finish.
    public void recieveMessage(String message)
    {
        // The only message we will recieve is the opponents
        // time so calculate it here.
        mOpponentTimeMillis = Long.parseLong(message);
        mOpponentTime = timeToString(mOpponentTimeMillis);
        
        mRaceTimeRecieved = true;
    }

    // This function will initialize our game and game objects
    private void gameInitialize()
    {      
        // we want ot render in full screen
        setFullScreenMode(true);
       
        // setup our camera and world group
        mGameCam = new GameCamera();
        mGameFont = Font.getFont(Font.FACE_MONOSPACE, 
                   Font.STYLE_BOLD, Font.SIZE_LARGE);
        
        try
        {                        	
            mTitleScreen = Image.createImage("/menus/title.png");
            mGameWon = new Sprite(Image.createImage("/menus/youWin.png"));
            mGameWon.setPosition(getWidth()/2-mGameWon.getWidth()/2, getHeight()/2-mGameWon.getHeight()/2);
            mGameLost = new Sprite(Image.createImage("/menus/youLoose.png"));
            mGameLost.setPosition(getWidth()/2 - mGameLost.getWidth()/2, getHeight()/2-mGameLost.getHeight()/2);
            mGameOptions = new Sprite(Image.createImage("/menus/gameOptions240x68.png"), 240, 68);
            mGameOptions.setPosition(0, getHeight()-mGameOptions.getHeight()-10);
            mGamePickShipOptions = new Sprite(Image.createImage("/menus/pickShipOptions.png"), 240, 68);
            mGamePickShipOptions.setPosition(0, getHeight()-mGamePickShipOptions.getHeight()-10);
            mGamePickShipOptions.nextFrame();

            // load the starting line box
            mStartingLine = new Sprite(Image.createImage("/menus/startingLine.png"));
            mStartingLine.setPosition(getWidth()/2-mStartingLine.getWidth()/2, getHeight()-mStartingLine.getHeight());
            
            // load other racer textures
            Texture2D car2Tex = new Texture2D((Image2D)Loader.load("/racers/car2.png")[0]);
            Texture2D car3Tex = new Texture2D((Image2D)Loader.load("/racers/car3.png")[0]);
        
            // now we load our 3D level and break it up into room
            // objects that we can mainpulate later
            Object3D[] buffer = Loader.load("/track/straight.m3g");
            for(int i = 0; i < buffer.length; i++)
            {
                if(buffer[i] instanceof World)
                {
                    World worldObject = (World)buffer[i];        
                    mTrack = (Mesh)worldObject.find(5);                    
                    mTrackTrans = new Transform();
                    
                    // turn off lighting on all the mTracks
                    // textures
                    Texture2D tmpTex = (Texture2D)worldObject.find(15);
                    tmpTex.setBlending(Texture2D.FUNC_REPLACE);
                    tmpTex = (Texture2D)worldObject.find(23);
                    tmpTex.setBlending(Texture2D.FUNC_REPLACE);
                    tmpTex = (Texture2D)worldObject.find(31);
                    tmpTex.setBlending(Texture2D.FUNC_REPLACE);
                    tmpTex = (Texture2D)worldObject.find(34);
                    tmpTex.setBlending(Texture2D.FUNC_REPLACE);
                    break;
                }
            }

            buffer = Loader.load("/racers/Car.m3g");
            for(int i = 0; i < buffer.length; i++)
            {
                if(buffer[i] instanceof World)
                {
                    World worldObject = (World)buffer[i];        
                    Mesh tmpMesh = (Mesh)worldObject.find(5);
                    mPlayerRacer = new Racer(tmpMesh, car2Tex, car3Tex, 0, 0, 0); 
                    break;
                }
            }
        }
        catch(Exception e) { System.out.println("Canvas Loading error: " + e.getMessage()); }
        
        // Here we get the instance to our graphics object
        mG3D = Graphics3D.getInstance();
        
        // We create our background that will be used to clear
        // the screen and we set it to black
        mBackground = new Background();
        mBackground.setColor(254 << 24 | 0  << 16 | 0 << 8 | 0);
                
        // Setup our HUD and Frames per sec calc
        FPSCounter.initialize();       
        
        // checks if we have sent or recieved the race time
        mRaceTimeSent = false;
        mRaceTimeRecieved = false;
        mOpponentTime = "";
        mOpponentTimeMillis = 0;

        RaceStarter.initialize();
        mCanIncrease = true;
        mCam2Set = false;
        mCam3Set = false;
        mGameState = GAMESTATE_CONNECT;
    }
    
    // this function will be used to update our game each frame.  All logic
    //  and animation updates can go here
    private void gameUpdate() 
    {
        // Update the stuff we wanto always update each frame
        FPSCounter.update();
        checkInput();

        // we get the players racer's position here so that 
        // it is only retrieved once for all the tests 
        // below. (instead of creating a new float[3] each
        // frame)
        mRacerXYZ = mPlayerRacer.getPosition();
        if (mGameState == GAMESTATE_SELECTCAR)
        {
            // we are picking what car we want to use
            // we just rotate the car for effect.  
            // Also this sets the player's starting
            // position to something different each time
            // they place their car at the starting line
            mPlayerRacer.rotate();
        }
        else if (mGameState == GAMESTATE_PLACECAR)
        {
            // The player needs to stay within bounds when
            // placing their car at their chosen starting spot
            
            // check X positions
            if (mRacerXYZ[0] < -1.22f)
            {
                mPlayerRacer.setPosition(-1.15f, mRacerXYZ[1], mRacerXYZ[2]);
            }
            else if (mRacerXYZ[0] > 1.22f)
            {
                mPlayerRacer.setPosition(1.15f, mRacerXYZ[1], mRacerXYZ[2]);
            }
            
            // Check Z position
            if (mRacerXYZ[2] > 10)
            {
                mPlayerRacer.setPosition(mRacerXYZ[0], mRacerXYZ[1], 10);
            }
            else if (mRacerXYZ[2] < 7)
            {
                mPlayerRacer.setPosition(mRacerXYZ[0], mRacerXYZ[1], 7);
            }
        }
        else if (mGameState == GAMESTATE_STARTINGLINE)
        {
            // The object that starts the race and keeps mTrack of the time
            // is updated here
            RaceStarter.update();

            // update our car (it moves at a constant speed that
            // is changed by the player
            mPlayerRacer.update();
            
            // update the timer object
            GameTimer.update();
            
            // at several points on the mTrack we will change
            // camera angles, positions and perspectives
            // This adds a great effect 
            if (mRacerXYZ[2] < 6 && mRacerXYZ[2] > -6.5f)
            {
                if (mCam2Set == false)
                {
                    mGameCam.setPosition(-1.25f, 2, -1);
                    mCam2Set = true;
                }
                // look at the racer while moving!
                mGameCam.lookAt(mRacerXYZ[0], mRacerXYZ[1], mRacerXYZ[2]);
            }
            
            // Camera 3 position!
            if (mRacerXYZ[2] < -4.5f && mRacerXYZ[2] > -9.5f)
            {
                if (mCam3Set == false)
                {
                    mGameCam.setPosition(0, 0, -11);
                    mGameCam.lookDown(-40);
                    mGameCam.rotate(180, 0, 1, 0);
                    mCam3Set = true;
                }
            }
            
            // Here is the finish line!
            if (mRacerXYZ[2] < -9)
            {
                GameTimer.stop();
                mGameState = GAMESTATE_ENDRACE;
            }
            
            // Now we need to make sure that the player does not 
            // go to far left or right.  If he does we stop him from 
            // moving through the wall and then we rotate him in the
            // correct direction
            if (mRacerXYZ[0] < -1.22f)
            {
                mPlayerRacer.setPosition(-1.15f, mRacerXYZ[1], mRacerXYZ[2]);
                mPlayerRacer.rotate(-10, 0, 1, 0);
                mPlayerRacer.addFriction();
            }
            if (mRacerXYZ[0] > 1.22f)
            {
                mPlayerRacer.setPosition(1.15f, mRacerXYZ[1], mRacerXYZ[2]);
                mPlayerRacer.rotate(10, 0, 1, 0);
                mPlayerRacer.addFriction();
            }
        }
        else if (mGameState == GAMESTATE_ENDRACE)
        {
            // The race has ended.  Send out our time if
            // it hasn't alreay been sent
            if (!mRaceTimeSent)
            {
                if (mConnected)
                {
                    String sendTime = "" + GameTimer.getElapsedTime();
                    mHoverNet.sendMessage(sendTime);
                    mRaceTimeSent = true;
                }
                
                mMyTime = timeToString(GameTimer.getElapsedTime());
            }
        }
    }
        
    // this is the rendering function in our Canvas.  It is sent the Graphics
    //  object so we can use it to display our game objects
    private void gameDraw(Graphics g) 
    {
        // drawing code goes here
        if (mGameState == GAMESTATE_CONNECT)
        {
            // This displays our connection status
            // The game can be played without a connection
            // by pressing Fire at any time.
            g.setColor(255, 255, 255);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(0, 0, 0);
            g.drawString(mStatus, 10, 50, Graphics.TOP|Graphics.LEFT);
            g.drawString("Press fire to continue / skip...", 10, 65, Graphics.TOP|Graphics.LEFT);
        }
        else if (mGameState == GAMESTATE_TITLEMENU)
        {
            // Display the opening menu
            g.drawImage(mTitleScreen, 0, 0, Graphics.TOP|Graphics.LEFT);
            mGameOptions.paint(g);
        }
        else if (mGameState == GAMESTATE_SELECTCAR)
        {
            // This draws the currently selected car 
            try
            {
                // First bind the graphics object. We use our pre-defined rendering hints.
                mG3D.bindTarget(g, true, Graphics3D.ANTIALIAS|Graphics3D.TRUE_COLOR|Graphics3D.DITHER);

                // clear the surface
                mG3D.clear(mBackground);

                // set our camera becasue we probably moved
                mG3D.setCamera(mGameCam, mGameCam.getTransform());
            
                mPlayerRacer.draw(mG3D);
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
            
            // draw options sprite (Piuck next car or continue!)
            mGamePickShipOptions.paint(g);
        }
        else if (mGameState == GAMESTATE_PLACECAR)
        {
            // Before the race begins the player placed
            // his car where he wanted it to start and with
            // a specific angle (its hard to get 90 deg!) 
            
            try
            {
                // First bind the graphics object. We use our pre-defined rendering hints.
                mG3D.bindTarget(g, true, Graphics3D.ANTIALIAS|Graphics3D.TRUE_COLOR|Graphics3D.DITHER);

                // clear the surface
                mG3D.clear(mBackground);

                // set our camera becasue we probably moved
                mG3D.setCamera(mGameCam, mGameCam.getTransform());

                mG3D.render(mTrack, mTrackTrans);
                mPlayerRacer.draw(mG3D);
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
            
            g.setFont(mGameFont);
            g.setColor(254, 175, 6);
            g.drawString("Place Your Car", 65, 190, Graphics.TOP|Graphics.LEFT);
            mStartingLine.paint(g);
        }
        else if (mGameState == GAMESTATE_STARTINGLINE)
        {
            // The game is starting now and the starting light
            // will paly through its animation and the mTrack
            // and car will be drawn.  This is also where
            // the game's driving takes place
            
            try
            {
                // First bind the graphics object. We use our pre-defined rendering hints.
                mG3D.bindTarget(g, true, Graphics3D.ANTIALIAS|Graphics3D.TRUE_COLOR|Graphics3D.DITHER);

                // clear the surface
                mG3D.clear(mBackground);

                // set our camera becasue we probably moved
                mG3D.setCamera(mGameCam, mGameCam.getTransform());

                mG3D.render(mTrack, mTrackTrans);
                mPlayerRacer.draw(mG3D);
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
            
            // draw the 2D starting sprite and time
            //  as well as the cars current speed
            RaceStarter.draw(g);
            g.drawString("Speed: " + (int)(mPlayerRacer.getSpeed()*1000), getWidth()-80, getHeight()-30, Graphics.TOP|Graphics.LEFT);
        }
        else if (mGameState == GAMESTATE_ENDRACE)
        {
            // Race is over so draw the times, and 
            // a message to who won and who lost
            try
            {
                // First bind the graphics object. We use our pre-defined rendering hints.
                mG3D.bindTarget(g, true, Graphics3D.ANTIALIAS|Graphics3D.TRUE_COLOR|Graphics3D.DITHER);

                // clear the surface
                mG3D.clear(mBackground);

                // set our camera becasue we probably moved
                mG3D.setCamera(mGameCam, mGameCam.getTransform());

                mG3D.render(mTrack, mTrackTrans);
                mPlayerRacer.draw(mG3D);
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
            
            if (mConnected == false)
            {
                g.drawString("My Time:    " + mMyTime, 30, 10, Graphics.TOP|Graphics.LEFT);
                g.setColor(255, 0, 0);
                g.drawString("Press End To Quit", 50, 220, Graphics.TOP|Graphics.LEFT);
            }
            else if (mRaceTimeRecieved)
            {
                // only draw stats once both players have finished
                if (GameTimer.getElapsedTime() < mOpponentTimeMillis)
                {
                    mGameWon.paint(g);
                    
                    // vibrate the winners phone!
                    vibrateAndFlash(5); 
                }
                else
                    mGameLost.paint(g);
                    
                g.drawString("My Time:    " + mMyTime, 30, 10, Graphics.TOP|Graphics.LEFT);
                g.drawString("Their Time: " + mOpponentTime, 30, 23, Graphics.TOP|Graphics.LEFT);
                
                g.setColor(255, 0, 0);
                g.drawString("Press End To Quit", 50, 220, Graphics.TOP|Graphics.LEFT);
            }
            else
            {
                // Let the player know what is going on and
                // why nothing is happening.
                g.drawString("Waiting for opponent to finish...", 5, 60, Graphics.TOP|Graphics.LEFT);
            }
        }
        
        // Now Draw 2D
        FPSCounter.draw(g); 

        /*// Draw the racers xyz coordinates if desired
        g.drawString("x: " + mRacerXYZ[0] + " y: " + mRacerXYZ[1] 
                     + " z: " + mRacerXYZ[2], 0, 40, 
                     Graphics.TOP|Graphics.LEFT);*/
  
        // flush to make visible             
        flushGraphics();
    }
    
    // This sets up the car selection state
    // by setting the camera position
    // the car position, and the actual state
    public void startSelectCar()
    {
        mGameCam.setPosition(0, 0, 1.6f);
        mPlayerRacer.setPosition(0, 0, 0);
        mGameState = GAMESTATE_SELECTCAR;
    }
    
    // This is the same thing for the car placing state
    public void startPlaceCar()
    {
        mPlayerRacer.setSpeed(0.6f);
        mGameCam.setPosition(0, 3, 8);
        mGameCam.lookAtGround();
        mPlayerRacer.setPosition(0, -1, 8);
        mGameState = GAMESTATE_PLACECAR;
    }
    
    // this gets the game ready to start racing with
    // by setting positions, and inital camera angles
    // as well as the actual state.  It then starts
    // the game object that controls starting the race
    public void startStartingLine()
    {
        mPlayerRacer.setSpeed(0);
        mGameCam.reset();
        mGameCam.setPosition(0, 0, 11);
        mGameCam.lookDown(20);
        mGameState = GAMESTATE_STARTINGLINE;
        RaceStarter.start(); // start the race!
    }

    // We use this function to do a basic conversion of 
    // the system time in milliseconds to the hours, minuites,
    // seconds, and milliseconds so they can be rendered. 
    // individually as strings
    private String timeToString(float time)
    {
        int hours = (int)(time / 3600000);
        if (hours != 0)
            time -= 3600000;
        int mins = (int)(time / 60000);
        if (mins != 0)
            time -= 60000;
        int secs = (int)(time / 1000);
        if (secs != 0)
            time -= 1000;
        int millis = (int)time / 100;
        
        String tmp = mins + "' " + secs + "' " + millis;
        return tmp;
    }
    
    // This is called in the MIDlet and gets our GameCanvas started
    public void start() 
    {
	// set the display object to this canvas
	mDisplay.setCurrent(this);

        // we call our own initialize function to setup all game objects
	gameInitialize();

	// Setup the thread and get it started
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

    // Our input function is much more complex this time around
    // We check each key, but depending on what state our game 
    // is in, each key will act differeently.  However, this
    // should be nothing new to us because we have done this
    // with our previous menu systems.
    private void checkInput()
    {        
        int keyState = getKeyStates();
        if ((keyState & FIRE_PRESSED) != 0)
        {
            mCurrentTime = System.currentTimeMillis();
            if (mCurrentTime > (mLastKeyPress+mFireKeyPressDelay))
            {
                if (mGameState == GAMESTATE_TITLEMENU)
                {
                    if (mGameOptions.getFrame() == 0)
                        startSelectCar();
                    else
                        HoverRacerMidlet.kill();
                }
                else if (mGameState == GAMESTATE_SELECTCAR)
                {
                    // When we 'change cars' we really just switch
                    // textures.  We could eventually implement
                    // different cars to have different advantages
                    // and disadvantages
                    if (mGamePickShipOptions.getFrame() == 0)
                    {
                        mPlayerRacer.nextTexture();
                    }
                    else
                        startPlaceCar();
                }
                else if (mGameState == GAMESTATE_PLACECAR)
                {
                    // The player moves his car to the place he
                    // wants to start from.  But Check if player 
                    // is within the starting line box
                    float[] playerXYZ = new float[3];
                    playerXYZ = mPlayerRacer.getPosition();
                    if (playerXYZ[0] > -1.2f && playerXYZ[0] < 1.25f)
                    {
                        if (playerXYZ[2] > 9 && playerXYZ[2] < 10.2f)
                            startStartingLine();
                    }
                }
                else if (mGameState == GAMESTATE_STARTINGLINE)
                {
                    // We are at the starting line so we check
                    // if the race is actually started.  If so 
                    // then we are able to move so we increase
                    // speed with each press of the fire key
                    // but we only allow this to happen once per 
                    // key press.  You must release and perss again
                    // to move faster.  
                    if (RaceStarter.movementEnabled() == true)
                    {
                        if (mCanIncrease == true)
                        {
                            mPlayerRacer.increaseSpeed();
                            mCanIncrease = false;
                        }
                    }
                }
                else if (mGameState == GAMESTATE_CONNECT)
                {
                    // We either connected and want to continue
                    // or we want to skip the connection process
                    // and finish the game.
                    mGameState = GAMESTATE_TITLEMENU;
                }

                mLastKeyPress = mCurrentTime;
            }
        } 
        
        // Here we check if the fire key has been released
        // if so we decrease the speed until it is zero.
        // it will remain at zero until the key is pressed 
        // again.
        if ((keyState & FIRE_PRESSED) == 0)
        {
            mPlayerRacer.addFriction();
            mCanIncrease = true;
        }

        // Here is basic movement of the menus
        // as well as the racer (when in the PLACECAR state)
        if ((keyState & UP_PRESSED) != 0) 
        {            
            // Don't delay the players movement code
            if (mGameState == GAMESTATE_PLACECAR)
                mPlayerRacer.moveForward();

            // delay the menu key presses to prevent choices
            // being hard to select
            mCurrentTime = System.currentTimeMillis();
            if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
            {
                if (mGameState == GAMESTATE_TITLEMENU)
                    mGameOptions.nextFrame();
                else if (mGameState == GAMESTATE_SELECTCAR)
                    mGamePickShipOptions.nextFrame();
                
                mLastKeyPress = mCurrentTime;
            }
        }
        if ((keyState & DOWN_PRESSED) != 0)
        {
            if (mGameState == GAMESTATE_PLACECAR)
                mPlayerRacer.moveBackward();

            mCurrentTime = System.currentTimeMillis();
            if (mCurrentTime > (mLastKeyPress+mKeyPressDelay))
            {
                if (mGameState == GAMESTATE_TITLEMENU)
                    mGameOptions.nextFrame();
                else if (mGameState == GAMESTATE_SELECTCAR)
                    mGamePickShipOptions.nextFrame();
                            
                mLastKeyPress = mCurrentTime;
            }
        }
        if ((keyState & LEFT_PRESSED) != 0)
        {
            if (mGameState == GAMESTATE_PLACECAR)
                mPlayerRacer.turnLeft();
        }
        if ((keyState & RIGHT_PRESSED) != 0)
        {
            if (mGameState == GAMESTATE_PLACECAR)
                mPlayerRacer.turnRight();
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
} // end of class