/*
 * SpaceInvadersCanvas.java
 *
 * This is our main canvas that we draw all our objects on.  Also it handles the 
 *  main game loop as well as the game logic.
 *
 */
import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.Graphics; // needed for graphics rendering
import javax.microedition.lcdui.game.GameCanvas; // needed to use the GameCanvas
import java.io.*;
import java.util.*;

/**
 * @author David Nelson
 */
// Here we declare our class and say that it extends the GameCanvas and
//  implements Runnable.  Whe are using Runnable here so that we can do all
//  our animation in a seperate thread.
public class SpaceBustersCanvas extends GameCanvas implements Runnable 
{
    // Declare our screen size so we can reference it throughout the game
    public static final int SCREEN_WIDTH  = 240;
    public static final int SCREEN_HEIGHT = 320;
    
    // We can change the screen size and do small tweaks to change our game
    //  to work on other size screens.
    //public static int SCREEN_WIDTH  = 176;
    //public static int SCREEN_HEIGHT = 220;
    
    // We use this static variable to determine if the player has won the game
    public static boolean gameWin = false;
    
    private Display mDisplay; // hold the display object from the MIDlet
    private Font mGameFont; // hold the font that we will use for our game
    
    private StarManager mStarManager;
    private EnemyManager mEnemyManager;
    private PlayerShip mPlayerShip;
    
    // This constructor recieves the display so we can set it later in our thread
    public SpaceBustersCanvas(Display d) 
    {
	super(true); // call base class constructor
	mDisplay = d; // store display object
    }
    
    // This function will initialize our game and game objects
    private void Gameinitialize()
    {      
        // set to fullscreen to use the entire display or false to only use
        //  the area inbetween the two bars on the display
	setFullScreenMode(true);
        
        // create a game font
        mGameFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        
        // Initialize our managers
        mStarManager = new StarManager();
        mEnemyManager = new EnemyManager();

        // Initialize our static manager class
        PlayerAttackManager.initialize();
        
        // Create our player object and ship!
        try
        {
            mPlayerShip = new PlayerShip(Image.createImage("/Player/Player.png"), 
                    this.SCREEN_WIDTH/2-30, this.SCREEN_HEIGHT-35, 100);
        }
        catch(IOException e)
        {
            System.out.println("Error!");
            System.exit(1);
        }
    }
    
    // this function will be used to update our game each frame.  All logic
    //  and animation updates can go here
    private void gameUpdate() 
    {
        // Update starts
        mStarManager.update();
        
        // If we haven't won yet keep playing! (Update everything)
        if (this.gameWin == false)
        {
            mEnemyManager.update(mPlayerShip);
            
            if (mPlayerShip.isAlive()) // only able to attack if alive!
            {
                checkInput();
                PlayerAttackManager.update();
            }
        }
    }

    // this is the rendering function in our Canvas.  It is sent the Graphics
    //  object so we can use it to display our game objects
    private void gameDraw(Graphics g) 
    {
	// clear the display
        g.setColor(0x000000); // black
	g.fillRect(0, 0, getWidth(), getHeight()); // fill with a rectangle

	// drawing code goes here
        g.setFont(mGameFont);
        g.setColor(0, 255, 0);
        
        // Always draw stars in the background
        mStarManager.drawStars(g);
        
        // If all the enemies are killed we will draw the ship and
        //  draw a message to the screen.
        if (this.gameWin)
        {
            mPlayerShip.draw(g);
            g.drawString("You Win!", this.getWidth()/2, this.getHeight()/2, Graphics.TOP|Graphics.HCENTER); 
        }
        else if (mPlayerShip.isAlive() == false) // player died so draw enemies and game over msg
        {
            mEnemyManager.draw(g);
            g.setColor(255, 0, 0);
            g.drawString("Game Over", this.getWidth()/2, this.getHeight()/2, Graphics.TOP|Graphics.HCENTER);
        }
        else
        {
            // We are in game so draw everything
            mEnemyManager.draw(g);
            PlayerAttackManager.draw(g);
            mPlayerShip.draw(g);
        }
        // end of drawing code
        
        // Flush the offscreen graphics buffer
        flushGraphics();
    }
    
    // This is called in the MIDlet and gets our GameCanvas started
    public void start() 
    {
	// Set the canvas as the current phone's screen
	mDisplay.setCurrent(this);

        // we call our own initialize function to setup all game objects
	Gameinitialize();

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
            try 
            {
                Thread.sleep(33); // sleep to sync the framerate on all devices
            }
            catch (InterruptedException ie) 
            {}
	}   
    }

    // Here we only use the attack, left, and right keys to move and fire.  We also check our bounds here
    //  because we only need to check if we are past our bounds if we have changed positions.  It would be
    //  quite slow to constantly check if we are out of bounds every frame.
    private void checkInput()
    {
        int keyState = getKeyStates();
        if ((keyState & FIRE_PRESSED) != 0)
        {
            PlayerAttackManager.fireBullet(mPlayerShip.getX()+mPlayerShip.getWidth()/2, mPlayerShip.getY());
        }
        if ((keyState & UP_PRESSED) != 0) 
        {
        }
        if ((keyState & DOWN_PRESSED) != 0)
        {
        }
        if ((keyState & LEFT_PRESSED) != 0)
        {
            mPlayerShip.move(-6, 0);
            if (mPlayerShip.getX() < 0)
                mPlayerShip.setPosition(0, mPlayerShip.getY());
        }
        if ((keyState & RIGHT_PRESSED) != 0)
        {
            mPlayerShip.move(6, 0);
            if (mPlayerShip.getX() > SpaceBustersCanvas.SCREEN_WIDTH-mPlayerShip.getWidth())
                mPlayerShip.setPosition(SpaceBustersCanvas.SCREEN_WIDTH-mPlayerShip.getWidth(), mPlayerShip.getY());
        }
    }
} // end of class