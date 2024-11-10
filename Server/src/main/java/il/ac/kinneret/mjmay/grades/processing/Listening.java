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
                // TODO: Listen for incoming connections
                // TODO: Handle them one at a time via a HandleClient instance.
            }
        } catch (Exception e) {
            // problem with this connection, show the output and quit
            System.err.println("Error listening for connections: " + e.getMessage());
        }
    }
}
