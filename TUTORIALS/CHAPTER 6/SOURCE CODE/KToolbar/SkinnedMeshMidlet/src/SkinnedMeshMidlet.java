// * HelloWorldTextMidlet.java
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class SkinnedMeshMidlet extends MIDlet implements CommandListener
{
    // we declare an instance of our Canvas (actually a GameCanvas!)
    private SkinnedMeshCanvas mCanvas;
    
    // Required function because of extending the MIDlet class.  Here
    //  we setup our canvas and create an exit command so we are able to 
    //  leave our MIDlet at any time.
    public void startApp() 
    {
        if (mCanvas == null)
        {
            // create the canvas
            mCanvas = new SkinnedMeshCanvas(Display.getDisplay(this));
            
            // Here we create the commands and setup the command listener
            //  This is discussed in more detail in the explantion of MIDlets
            Command exit = new Command("Exit", Command.EXIT, 0);
            mCanvas.addCommand(exit);
            mCanvas.setCommandListener(this);
        }
        
        // call the Canvas' start method to get the appliation rolling
        mCanvas.Start();
    }
    
    // Called whenever the application is paused
    public void pauseApp() 
    {
    
    }
    

    // This is called when we want to destroy the application or for some reason
    //  the phone is asking for it to be destroyed.  The unconditional paramater
    //  lets us know if we have a say in the matter
    public void destroyApp(boolean unconditional) 
    {
        // Cleanup and prepare to be destroyed!
    }
    
    // Implementing CommandListener requires us to implement this function
    // Here we recieve a command and check to see if it is our previously
    //  created exit command.  If so we destroy the application.
    public void commandAction(Command c, Displayable s)
    {
        if (c.getCommandType() == Command.EXIT)
        {
            // destroy the appp unconditionally
            destroyApp(true);
            // let the phone know we have been destroyed
            notifyDestroyed(); 
        }
    }
}
