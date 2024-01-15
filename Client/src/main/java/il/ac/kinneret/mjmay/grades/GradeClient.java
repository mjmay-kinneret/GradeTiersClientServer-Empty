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

        System.out.println("Connecting to the server.");
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(serverIP, port);
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            return;
        }

        System.out.println("Connected to the server.");

        // read from the keyboard
        BufferedReader brKeyboard = new BufferedReader(new InputStreamReader(System.in));

        try ( BufferedReader brNetwork = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
              PrintWriter pwNetwork = new PrintWriter(clientSocket.getOutputStream())) {
            while (true) {
                // get a TZ to look up
                System.out.print("Enter a TZ to look up (blank to quit): ");

                // if it's blank, quit
                String tzToLookup = brKeyboard.readLine();
                if (tzToLookup.trim().length() == 0)
                {
                    // blank line tells it's time to close
                    pwNetwork.println();
                    break;
                }

                // it's not blank, look it up
                pwNetwork.println(tzToLookup.trim());
                pwNetwork.flush();

                // wait for a response
                String resultLine = "";

                do {
                    // read the next line
                    resultLine = brNetwork.readLine();
                    if ( resultLine != null)
                    {
                        // show it if it's not null
                        System.out.println(resultLine.trim());
                    }
                    else
                    {
                        // something wacky happened
                        break;
                    }
                } while (resultLine.trim().length() > 0);

                // now get the next line
            }
        } catch (IOException iox)
        {
            System.out.println("Error in network communication: " + iox.getMessage());
        }
        finally {
            try { clientSocket.close(); } catch (Exception ex) {}
        }
        // all done!
        System.out.println("Closed connection and done.");
    }

    private static void showUsage()
    {
        System.out.println("Usage: GradeClient ServerIP ServerPort");
    }

}
