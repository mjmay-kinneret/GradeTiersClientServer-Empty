package il.ac.kinneret.mjmay.grades.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class HandleClient extends Thread {

    /**
     * The client connection socket
     */
    Socket clientSocket;

    /**
     * Builds an instance with a client connection
     * @param socket The client connection socket
     */
    public HandleClient (Socket socket)
    {
        clientSocket = socket;
    }

    public void run() {
        // TODO: Get the queries from the client
        // TODO: Create the database connections using DriverManager.getConnection();
        // TODO: Create queries using PreparedStatement and Connection.prepareStatement()
        // TODO: Add parameters using PreparedStatement.setInt()
        // TODO: Get results using the ResultSet class
        // TODO: Return the results to the client.  Close connection when done

        BufferedReader brIn = null;
        PrintWriter pwOut = null;
        try {
            brIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            pwOut = new PrintWriter(clientSocket.getOutputStream());

        } catch (IOException ex) {
            // this client isn't usable
            System.err.println("Error setting up connection with the client: " + clientSocket.getRemoteSocketAddress().toString()
                    + ": " + ex.getMessage());
            try {
                clientSocket.close();
            } catch (Exception e) {
            }
        }

        System.out.println("Got connection from: " + clientSocket.getRemoteSocketAddress());

    }
}
