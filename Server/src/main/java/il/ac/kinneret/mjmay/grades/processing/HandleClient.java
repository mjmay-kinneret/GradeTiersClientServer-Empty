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

    public void run()
    {
        // get the queries from the client

        BufferedReader brIn = null;
        PrintWriter pwOut = null;
        try {
            brIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            pwOut = new PrintWriter(clientSocket.getOutputStream());

        } catch (IOException ex) {
            // this client isn't usable
            System.err.println("Error setting up connection with the client: " + clientSocket.getRemoteSocketAddress().toString()
                    + ": " + ex.getMessage());
            try {clientSocket.close();} catch (Exception e) {}
        }

        System.out.println("Got connection from: " + clientSocket.getRemoteSocketAddress());

        // the results go back as a list of Strings
        Vector<String> finalGradeResults = new Vector<String>();

        //Connection connBooklets = null;
        //Connection connGrades = null;

        try ( Connection connBooklets = DriverManager.getConnection(Constants.connectionStringBooklets);
              Connection connGrades = DriverManager.getConnection(Constants.connectionStringGrades)){
            // get all the queries that the client wants
            while (true)
            {
                // get the query from the client
                String query = brIn.readLine();

                // see if this is blank, if yes, client is done
                if ( query == null || query.trim().length() == 0)
                {
                    break;
                }

                // reset the results set
                finalGradeResults.clear();

                // the query should be a TZ
                int studentId = 0;
                try {
                    studentId = Integer.parseInt(query);
                } catch (NumberFormatException nfe)
                {
                    // something is screwy, just send back a blank and try again
                    pwOut.println();
                    pwOut.flush();
                    continue;
                }
                //query based on the student id provided
                System.out.println(clientSocket.getRemoteSocketAddress() + ": Query for TZ " + studentId);


                // prepare the query for the booklets database
                PreparedStatement bookletsStatement = connBooklets.prepareStatement(Constants.bookletQuery);
                bookletsStatement.setInt(1,  studentId);
                ResultSet bookletsResults = bookletsStatement.executeQuery();

                // get the booklets for the student
                while (bookletsResults.next())
                {
                    // get the results from the second database for the booklet number
                    PreparedStatement gradesStatement = connGrades.prepareStatement(Constants.gradesQuery);
                    gradesStatement.setInt(1, bookletsResults.getInt(Constants.BOOKLET_NUMBER_COLUMN));

                    ResultSet gradesResults = gradesStatement.executeQuery();

                    // see if there are any results
                    if (gradesResults.next())
                    {
                        // make a line for it
                        String bookletGradeLine = String.format(Constants.resultsFormat, bookletsResults.getLong(Constants.TZ_COLUMN),
                                bookletsResults.getString(Constants.STUDENT_NAME_COLUMN), bookletsResults.getString(Constants.COURSE_NAME_COLUMN),
                                bookletsResults.getInt(Constants.YEAR_COLUMN), bookletsResults.getInt(Constants.SEMESTER_COLUMN),
                                bookletsResults.getInt(Constants.BOOKLET_NUMBER_COLUMN),
                                (gradesResults.getInt(Constants.CHECKED_COLUMN) == 0 ? "No" : "Yes"),
                                gradesResults.getInt(Constants.GRADE_COLUMN));
                        // enter it into the vector
                        finalGradeResults.addElement(bookletGradeLine);

                        System.out.println(clientSocket.getRemoteSocketAddress() + ": Result: " + bookletGradeLine);
                    }
                    // close up the results from the booklet
                    gradesResults.close();
                }
                // now close up the booklets results
                bookletsResults.close();

                // now return the results to the client
                for (String gradeLine : finalGradeResults)
                {
                    pwOut.println(gradeLine);
                }

                // send an empty line to indicate we're done
                pwOut.println();
                pwOut.flush();
            } // done with this query, get the next one
        }
        catch (IOException ex)
        {
            // something is wrong
            System.err.println("Error communicating with the client: " + clientSocket.getRemoteSocketAddress().toString()
                    + ": " + ex.getMessage());
            try {clientSocket.close();} catch (Exception e) {}
        } catch (SQLException e) {
            System.err.println("Error querying databases: " + e.getMessage());
        }
        finally
        {
            // close up shop
            if (clientSocket != null)
            {
                try
                {
                    System.out.println(clientSocket.getRemoteSocketAddress() + " closing.");
                    clientSocket.close();
                } catch (Exception ex) {}
            }
        }
    }

}
