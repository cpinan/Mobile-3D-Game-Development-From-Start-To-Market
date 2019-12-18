import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

// This is our basic MIDlet class with a little extra beef added
// to it for networking abilities.  We are also using
// a GUI control that we have not dicussed.
public class HoverRacerMidlet extends MIDlet implements CommandListener
{
    // we declare our game canvas and self object so we can 
    // kill the midlet later! :-)
    private HoverRacerCanvas mCanvas;
    private static MIDlet mSelf;
    
    // This is our GUI form and a choice group
    // This will allow the user to pick if 
    // he wants to make his phone the server
    // or client
    private Form mForm;
    private ChoiceGroup mChoices;
    
    // just as before we have our required startApp()
    // however we create our form. adn add the choice
    // objects to it
    public void startApp() 
    {
        // create a default for with our title
        mForm = new Form("Hover Networking Game");
        
        // add the selection circles (choice group)
        String[] peerNames = { "Server", "Client" };
        mChoices = new ChoiceGroup("Choose Game Type:", Choice.EXCLUSIVE,
                peerNames, null);
        mForm.append(mChoices);
        
        // now the player can choose to exit 
        // OR connect to the other object so we add these
        // commands to our Form.
        Command exitCommand = new Command("Exit", Command.EXIT, 0);
        mForm.addCommand(exitCommand);
        Command playCommand = new Command("Connect", Command.OK, 0);
        mForm.addCommand(playCommand);
        mForm.setCommandListener(this);
        
        // now we set our display to this form
        Display.getDisplay(this).setCurrent(mForm);
        
        // set the self like we did in Chapter 9
        // this will allow us to kill our midlet 
        // from any point in the game.
        mSelf = this;
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
    
    // We now need to process our EXIT command as well as 
    // our other commands that handle choosing either server
    // or client (the choice group we placed on our form)
    public void commandAction(Command c, Displayable s)
    {
        if (c.getCommandType() == Command.EXIT)
        {
            destroyApp(true);
            notifyDestroyed();
        }
        else if (c.getCommandType() == Command.OK)
        {
            // We selected either client or server
            // to figure it out and send it to our 
            // canvas so our networking can be created
            // correctly
            String type = mChoices.getString(mChoices.getSelectedIndex());
            
            if (mCanvas == null)
            {
                // give the canvas the display object
                // as well as the type (server, client) that we are.
                mCanvas = new HoverRacerCanvas(Display.getDisplay(this), type);
                Command exitCommand = new Command("Exit", Command.EXIT, 0);
                mCanvas.addCommand(exitCommand);
                mCanvas.setCommandListener(this);
            }
            
            mCanvas.start();
        }
    }
    
    // This static function will kill the midlet at any time
    public static void kill()
    {
        mSelf.notifyDestroyed();
    }
}
