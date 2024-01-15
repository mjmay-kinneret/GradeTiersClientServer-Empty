package il.ac.kinneret.mjmay.grades.processing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listening extends Thread {

    /**
     * The listening socket
     */
    ServerSocket listeningSocket;

    /**
     * Initializes the listening thread
     * @param serverSocket The server socket to listen on
     */
    public Listening (ServerSocket serverSocket)
    {
        // save the socket we've been provided
        listeningSocket = serverSocket;
    }

    @Override
    public void run()
    {
        // start to listen on the socket
        try {
            while (true)
            {
                Socket clientSession = listeningSocket.accept();

                // see if we were interrupted - then stop
                if (this.isInterrupted())
                {
                    System.err.println("Stopped listening due to interruption.");
                    break;
                }
                // create a new handling thread for the client
                HandleClient clientThread = new HandleClient(clientSession);
                clientThread.start();
            }
        } catch (IOException e) {
            // problem with this connection, show the output and quit
            System.err.println("Error listening for connections: " + e.getMessage());
        }
    }
}
