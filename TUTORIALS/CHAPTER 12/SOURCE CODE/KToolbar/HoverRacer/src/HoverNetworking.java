/*
 * StormNetowrking.java
 *
 * Created on June 14, 2006, 1:06 AM
 *
 */
import java.util.*;
import java.io.*;
import javax.microedition.io.*;

public class HoverNetworking implements Runnable
{
    // We need to know if we are a server or 
    // a client so we can send messages
    // correctly
    private boolean mIsServer;

    // we keep an instance of our canvas
    // so we can send it messages
    private HoverRacerCanvas mCanvas;
    
    // This is our datagram connection object
    // as well as the information to connect
    // to the other device/server/etc
    private DatagramConnection mDgramConnection;
    private String mAddress;
    private boolean mConnected;
    
    // This accepts the canvas and if this is a server or not
    public HoverNetworking(HoverRacerCanvas canvas, boolean isServer) 
    {
        mCanvas = canvas;
        mIsServer = isServer;
        mConnected = false;
    }
    
    // We will have our thread loop indefinatly 
    // to check and see if messages need to be 
    // sent
    public void start()
    {
        Thread t = new Thread(this);
        t.start();
    }
    
    public void run()
    {
        try
        {
            // If we are a server we want to wait 
            // for a connection.  Once one is 
            // established we handshake with client
            // and process messages
            if (mIsServer)
            {
                mCanvas.setStatus("Waiting for a connection.");
                mDgramConnection = null;

                // get connection before we move forward
                while (mDgramConnection == null)
                    mDgramConnection = (DatagramConnection)Connector.open("datagram://:5555");

                // no process messages and data packets
                while(true)
                {
                    // Try and receive a packet
                    Datagram datagram = mDgramConnection.newDatagram(32);
                    mDgramConnection.receive(datagram);
                    mAddress = datagram.getAddress();

                    // is there data in this packet?
                    if (datagram.getLength() > 0)
                    {
                        String data = new String(datagram.getData(), 0, datagram.getLength());
                        if (data.equalsIgnoreCase("Client"))
                        {
                            // We're connected! woohoo!
                            mCanvas.setStatus("Connected to Client!");
                            mConnected = true;

                            // handshake back
                            sendClientMessage("Server");
                        }
                        else
                        {
                            // Here we send the message 
                            // to the canvas 
                            mCanvas.recieveMessage(data);
                            
                            // if we by chance wanted to sync
                            // two devices to say, rotate
                            // an object we could then send
                            // the message back to the other
                            // device.  We do not want this
                            // in our case though.
                            //sendClientMessage(data);
                        }
                    }
                }
            }// end of isServer
            else // we are a client!
            {
                // Same basic principle with the client except we send
                // messages to the server.
                mCanvas.setStatus("Connecting to server....");
                mDgramConnection = null;
                while(mDgramConnection == null)
                    mDgramConnection = (DatagramConnection)Connector.open("datagram://localhost:5555");
                
                while(true)
                {
                    // try to send a connection message
                    if (!mConnected)
                        sendServerMessage("Client");
                    
                    // try and recieve packets
                    Datagram datagram = mDgramConnection.newDatagram(32);
                    mDgramConnection.receive(datagram);
                    
                    // check if there is anything in the packet
                    if (datagram.getLength() > 0)
                    {
                        String data = new String(datagram.getData(), 0, datagram.getLength());
                        if (data.equalsIgnoreCase("Server"))
                        {
                            // we are connected so tell user
                            mCanvas.setStatus("Connected to server!");
                            mConnected = true;
                        }
                        else
                        {
                            // we are connected so pass data along to canvas
                            mCanvas.recieveMessage(data);
                        }
                    }
                }
            }// end of isClient
        }
        catch(IOException ioe)
        {
            System.err.println("The network port is already taken.");
        }
        catch(Exception e)
        {
            System.err.println("Error: " + e);
        }
    }

    // This is a simple wrapper function that allows us to
    // call one function for sending messages and it decides
    // what type of message to send based on if the device is
    // a client or server
    public void sendMessage(String message)
    {
        if (mIsServer)
            sendClientMessage(message);
        else
            sendServerMessage(message);
    }
    
    // send the server a message
    private void sendServerMessage(String message)
    {
        try
        {
            // convert message to bytes
            byte[] bytes = message.getBytes();
            
            // send the message
            Datagram datagram = null;
            
            // Notice that no address is required 
            datagram = mDgramConnection.newDatagram(bytes, bytes.length);
            mDgramConnection.send(datagram);
        }
        catch(Exception e)
        {
            System.err.println("Error: " + e);
        }
    }

    // Send the client the message
    private void sendClientMessage(String message)
    {
        try
        {
            // convert message to bytes
            byte[] bytes = message.getBytes();
            
            // send the message
            Datagram datagram = null;
            
            // notice that an address is required
            datagram = mDgramConnection.newDatagram(bytes, bytes.length, mAddress);
            mDgramConnection.send(datagram);
        }
        catch(Exception e)
        {
            System.err.println("Error: " + e);
        }
    }
}
