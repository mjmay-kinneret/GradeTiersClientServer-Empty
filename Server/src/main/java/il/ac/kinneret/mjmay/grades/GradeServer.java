package il.ac.kinneret.mjmay.grades;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

import il.ac.kinneret.mjmay.grades.processing.Constants;
import il.ac.kinneret.mjmay.grades.processing.Listening;

public class GradeServer {

    public static void main(String[] args) {

        // check that we have all parameters
        if (args.length != 2) {
            showUsage();
            return;
        }

        // we need to get a port from the user
        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            // something went wrong here. Quit.
            showUsage();
            return;
        }

        // read the configuration file
        try {
            BufferedReader configFileIn = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
            // read the connection strings
            String connectionString1 = configFileIn.readLine();
            Constants.setConnectionStringBooklets(connectionString1);
            String connectionString2 = configFileIn.readLine();
            Constants.setConnectionStringGrades(connectionString2);
            configFileIn.close();
            Class.forName("org.sqlite.JDBC");
        } catch (FileNotFoundException ex) {
            System.err.println("Error: can't open configuration file: " + args[1] + ": " + ex.getMessage());
            showUsage();
            return;
        } catch (IOException e) {
            System.out.println("Error reading configuration file: " + args[1] + ": " + e.getMessage());
            showUsage();
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("Can't initialize JDBC class instance.");
            return;
        }

        Vector<InetAddress> adds = getAvailableIPAddresses();
        if (adds == null) return;

        int choice = selectIPAddress(adds);

        // the listen/stop loop
        try (BufferedReader brKeyboard = new BufferedReader(new InputStreamReader(System.in))) {
            String lineIn = "";
            boolean quit = false;
            do {
                // start to listen on the one that the user chose
                ServerSocket listener = null;
                try {
                    listener = new ServerSocket(port, 50, adds.elementAt(choice));
                    Listening listening = new Listening(listener);
                    listening.start();

                } catch (IOException e) {
                    // fatal error, just quit
                    System.out.println("Can't listen on " + adds.elementAt(choice) + ":" + port);
                    break;
                }

                // listen for the command to stop listening
                do {
                    // we now have a working server socket, we'll use it later
                    System.out.println("Listening on " + listener.getLocalSocketAddress().toString());
                    System.out.println("Enter 'STOP' to stop listening");
                    lineIn = brKeyboard.readLine();

                } while (!lineIn.trim().equalsIgnoreCase("stop"));

                // stop listening
                listener.close();

                quit = checkQuit(brKeyboard);
            } while (!quit);
        } catch (Exception ex) {
            // this shouldn't happen
        }

        return;
    }

    private static boolean checkQuit(BufferedReader brKeyboard) throws IOException {
        String lineIn;
        // now we can resume listening if we want
        System.out.println("Resume listening? [y/n]");
        do {
            System.out.print(": ");
            lineIn = brKeyboard.readLine();
        } while (!lineIn.trim().equalsIgnoreCase("y") && !lineIn.trim().equalsIgnoreCase("n"));

        // see whether we have an n or a y
        if (lineIn.trim().equalsIgnoreCase("y")) {
            System.out.println("Resuming listening");
        } else {
            // quitting
            System.out.println("Bye!");
            return true;
        }
        return false;
    }

    private static int selectIPAddress(Vector<InetAddress> adds) {
        int choice = -1;
        System.out.println("Choose an IP address to listen on :");
        for (int i = 0; i < adds.size(); i++) {
            // show it in the list
            System.out.println(i + ": " + adds.elementAt(i).toString());
        }

        BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));

        while (choice < 0 || choice >= adds.size()) {
            System.out.print(": ");
            try {
                String line = brIn.readLine();
                choice = Integer.parseInt(line.trim());
            } catch (Exception ex) {
                System.out.print("Error parsing choice\n: ");
            }
        }

        return choice;
    }

    private static Vector<InetAddress> getAvailableIPAddresses() {
        // make a list of addresses to choose from
        // add in the usual ones
        Vector<InetAddress> adds = new Vector<>();
        try {
            adds.add(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}));
            adds.addElement(InetAddress.getLoopbackAddress());
        } catch (UnknownHostException ex) {
            // something is really weird - this should never fail
            System.out.println("Can't find IP address 0.0.0.0: " + ex.getMessage());
            return null;
        }

        try {
            // get the local IP addresses from the network interface listing
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                // see if it has an IPv4 address
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    // go over the addresses and add them
                    InetAddress add = addresses.nextElement();
                    if (!add.isLoopbackAddress()) {
                        adds.addElement(add);
                    }
                }
            }
        } catch (SocketException ex) {
            // can't get local addresses, something's wrong
            System.out.println("Can't get network interface information: " + ex.getMessage());
            return null;
        }
        return adds;
    }

    /**
     * Shows the usage for the tool.
     */
    private static void showUsage() {
        System.out.println("Usage: GradeServer port configFileName");
        try {
            System.in.read();
        } catch (IOException e) {
        }
    }

}
