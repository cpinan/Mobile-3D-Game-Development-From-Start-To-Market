import javax.microedition.lcdui.*; // for basic graphics, display, etc
import javax.microedition.lcdui.Graphics; // needed for graphics rendering
import javax.microedition.lcdui.game.GameCanvas; // needed to use the GameCanvas


// Here we declare our class and say that it extends the GameCanvas and
//  implements Runnable.  Whe are using Runnable here so that we can do all
//  our animation in a seperate thread.
public class HelloWorldTextCanvas extends GameCanvas implements Runnable 
{
    private Display mDisplay; // hold the display object from the MIDlet
    private Font mGameFont; // hold the font that we will use for our game
    
    private String mTextToWrite;
    private float mCounter;
    private String mCounterText;
    // This constructor recieves the display so we can set it later in our thread
    public HelloWorldTextCanvas(Display d) 
    {
	super(true); // call base class constructor
	mDisplay = d; // store display object
        
        mTextToWrite = "Hello World!";
        mCounter = 0.0f;
    }
    
    // This function will initialize our game and game objects
    private void GameInitialize()
    {      
	// set to fullscreen to use the entire display or false to only use
        //  the area inbetween the two bars on the display
	setFullScreenMode(true);
        
        // create a game font
        mGameFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_LARGE);
    }

    // this function will be used to update our game each frame.  All logic
    //  and animation updates can go here
    private void GameUpdate() 
    {
        mCounter++;
        mCounterText = String.valueOf(mCounter);
    }

    // this is the rendering function in our Canvas.  It is sent the Graphics
    //  object so we can use it to display our game objects
    private void GameDraw(Graphics g) 
    {
	// clear the display
        g.setColor(0xFFFFFF); // white
	g.fillRect(0, 0, getWidth(), getHeight()); // fill with a rectangle

	// drawing code goes here
        
        // set the text's color to black 
        g.setColor(0, 0, 0);
        
        // write text to the screen
        g.drawString(mTextToWrite, 20, 20, Graphics.TOP | Graphics.LEFT);
        g.drawString(mCounterText, 20, 40, Graphics.TOP | Graphics.LEFT);
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
            try 
            {
                Thread.sleep(33); // sleep to sync the framerate on all devices
            }
            catch (InterruptedException ie) 
            {}
	}   
    }
} // end of class
