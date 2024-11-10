package il.ac.kinneret.mjmay.grades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GradeClient {

    public static void main(String[] args) {

        InetAddress serverIP = null;
        int port = 0;

        try {
            // get the IP and port from the parameters
            serverIP = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
        }
        catch (UnknownHostException unx)
        {
            System.out.println("Error: Can't resolve host: " + unx.getMessage());
            showUsage();
            return;
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("Error: Invalid port: " + nfe.getMessage());
            showUsage();
            return;
        }
        catch (Exception ex)
        {
            showUsage();
            return;
        }

        // TODO: Connect to the server
        // TODO: Get a TZ from the user via keyboard
        // TODO: Retrieve the result from the server.  When the connection closes we're done
    }

    private static void showUsage()
    {
        System.out.println("Usage: GradeClient ServerIP ServerPort");
    }

}
