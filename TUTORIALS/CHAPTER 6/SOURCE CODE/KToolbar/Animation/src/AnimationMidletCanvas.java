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
import javax.microedition.m3g.AnimationTrack;
import javax.microedition.m3g.AnimationController;
import javax.microedition.m3g.KeyframeSequence;


public class AnimationMidletCanvas extends GameCanvas implements Runnable 
{
    public static final int SCREEN_WIDTH  = 240;
    public static final int SCREEN_HEIGHT = 320;

    private World mWorld;
    private Graphics3D mG3D;
    private Camera mCam;

    private Display mDisplay; 
    private Font mGameFont; 

    private Mesh mEnemy;
    private int mEnemyTimer;
    

    public AnimationMidletCanvas(Display d) 
    {
        super(true); 
        mDisplay = d; 
    }


    private void GameInitialize()
    {      
        setFullScreenMode(true);

        // setup 3D
        try
        {
            Object3D[] buffer = Loader.load("/ship.m3g");

            for(int i = 0; i < buffer.length; i++)
            {
                if(buffer[i] instanceof World)
                {
                    // get our world node
                    mWorld = (World)buffer[i];
                    mEnemy = (Mesh)mWorld.find(5);
                    mEnemyTimer = 0;
                    break;
                }
            }
        }
        catch(Exception e) 
        { System.out.println("Loading error!" + e.getMessage()); }


        mGameFont = Font.getFont(Font.FACE_MONOSPACE, 
                   Font.STYLE_PLAIN, Font.SIZE_SMALL);


        mCam = mWorld.getActiveCamera();
        mCam.translate(0, 0.0f, 0);

 
        Light light = new Light();
        light.setMode(Light.AMBIENT);
        light.setIntensity(3);
        mWorld.addChild(light);

        //create animation sequence with 4 keyframes
        KeyframeSequence shipKframeSeq= new KeyframeSequence(4, 3,
        KeyframeSequence.LINEAR);

        //populate the 4 keyframes with positional data
        shipKframeSeq.setKeyframe(0, 0, new float[] { 0.0f, 0.0f, 0.0f });
        shipKframeSeq.setKeyframe(1, 100, new float[] { 0.0f, 0.0f, 1.0f});
        shipKframeSeq.setKeyframe(2, 200, new float[] { 0.0f, 0.0f, 2.0f });
        shipKframeSeq.setKeyframe(3, 300, new float[] { 0.0f, 0.0f, -1.0f});
        shipKframeSeq.setDuration(2000);

        //create an animation track
        //associate it with Translation
        AnimationTrack moveTrack = new AnimationTrack(shipKframeSeq, 
        AnimationTrack.TRANSLATION);

        //add the track to our mesh
        mEnemy.addAnimationTrack(moveTrack);

        //create a new controller
        //set it to control the moveTrack
        AnimationController meshAnim = new AnimationController();
        moveTrack.setController(meshAnim);
 
        //set active period for track
        //set position
        meshAnim.setActiveInterval(1800, 4800);
        meshAnim.setPosition(0, 2100);

    }


    private void GameUpdate() 
    {        
 
        //animate our mesh
        mEnemy.animate(mEnemyTimer += 60);
    }


    private void GameDraw(Graphics g) 
    {

        g.setColor(0x000000); 
        g.fillRect(0, 0, getWidth(), getHeight()); 

        // drawing code goes here
        g.setFont(mGameFont);
        g.setColor(255, 255, 255);

        try
        {
            mG3D = Graphics3D.getInstance();
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

        flushGraphics();
    }

 
    public void Start() 
    {
        mDisplay.setCurrent(this);

        GameInitialize();

        Thread thread = new Thread(this);
        thread.start();
    }

 
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