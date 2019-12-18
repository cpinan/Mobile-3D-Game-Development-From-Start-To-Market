import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


public class IModeRenderingMidlet extends MIDlet implements CommandListener
{
    private IModeRenderingCanvas mCanvas;
    

    public void startApp() 
    {
        if (mCanvas == null)
        {

            mCanvas = new IModeRenderingCanvas(Display.getDisplay(this));
            

            Command exit = new Command("Exit", Command.EXIT, 0);
            mCanvas.addCommand(exit);
            mCanvas.setCommandListener(this);
        }

        mCanvas.start();
    }
    

    public void pauseApp() { }
    

    public void destroyApp(boolean unconditional) { }
    

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
