/*
 * FPSCounter.java
 *
 * Created on October 23, 2006, 6:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author David Nelson
 */
public class SavageCounter 
{   
    private static int fps_result = 0;
    private static int fps_counter = 0;
    private static long total_time = 0;
    private static long start_time = 0;
        
    // Initializes the Frames Per Second counter
    public static void Initialize()
    {
        fps_result = 0;
        fps_counter = 0;
        total_time = 0;
        
        start_time = System.currentTimeMillis();
    }
    
    public static void update()
    {
        long current_time = System.currentTimeMillis();
        long frame_time = current_time - start_time;
        start_time = current_time;
    
        total_time += frame_time;
        if (total_time >= 1000)
        {
            fps_result = fps_counter + 1;
            fps_counter = 0;
            total_time = 0;
        }
        fps_counter++;
    }
    
    public static void draw(Graphics g)
    {
        g.setColor(0, 0, 255);
        g.drawString("[ " + fps_result + " ]", 0, 280, Graphics.TOP | Graphics.LEFT);
    }
}
