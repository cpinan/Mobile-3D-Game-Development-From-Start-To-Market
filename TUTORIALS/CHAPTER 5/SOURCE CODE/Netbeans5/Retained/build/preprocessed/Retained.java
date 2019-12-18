import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Retained extends MIDlet implements CommandListener 
{
    private RetainedCanvas mRetCanvas;
    
    protected void startApp() throws MIDletStateChangeException 
    {
        mRetCanvas=new RetainedCanvas(Display.getDisplay(this));
        
        Command terminateCommand=new Command("Quit", Command.EXIT, 0);
        mRetCanvas.addCommand(terminateCommand);
        mRetCanvas.setCommandListener(this);
        
        mRetCanvas.start();
    }
    
    
    protected void pauseApp()
    { 
    }
    
    protected void destroyApp(boolean unconditional)  
    { 
    }
    
    public void commandAction(Command c, Displayable d) 
    {
        if(c.getCommandType() == Command.EXIT) 
        {
            destroyApp(true);
            notifyDestroyed();
        }
    }
}
