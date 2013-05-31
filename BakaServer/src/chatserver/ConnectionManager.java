/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edgar
 */
public class ConnectionManager implements Runnable
{

    public static final char PROTOCOL_SIGNUP = 0;
    public static final char PROTOCOL_SIGNUP_OK = 1;
    private Socket mySocket;
    private static LinkedList<Account> allLoggedAccounts = new LinkedList<>();
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionManager(Socket s)
    {
        mySocket = s;
    }

    @Override
    public void run()
    {
        try
        {
            out = new PrintWriter(mySocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    mySocket.getInputStream()));
            while (true)
            {
                switch (in.read())
                {
                    case PROTOCOL_SIGNUP:
                        StringBuilder msg_builder = new StringBuilder();
                        int parts = 0;
                        String msgParts[] = new String[2];
                        int msg_char;
                        do
                        {
                            msg_char = in.read();

                            switch (msg_char)
                            {
                                case 0:
                                    msgParts[parts++] = msg_builder.toString();
                                    msg_builder = new StringBuilder();
                                    break;
                                case -1:
                                    killConnection();
                                    return;
                                default:
                                    msg_builder.append(msg_char);
                                    break;
                            }
                        }
                        while (parts < 2);
                        MessageDigest md =
                                MessageDigest.getInstance("SHA-256");
                        md.update(msgParts[1].getBytes());
                        Account acc =
                                new Account(msgParts[0], md.digest());
                        out.print(PROTOCOL_SIGNUP_OK);
                        out.flush();
                        break;
                }
            }



        }
        catch (NoSuchAlgorithmException | IOException ex)
        {
            Logger.getLogger(ConnectionManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        killConnection();
    }

    private void killConnection()
    {
        try
        {
            in.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ConnectionManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        out.close();

        try
        {
            mySocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ConnectionManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }
}
