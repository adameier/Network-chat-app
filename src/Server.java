
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mthmas016
 */
public class Server extends Thread{
    
    ServerSocket serverSocket;
    Socket clientSocket = null;
    private static int portNum = 5678;
    DataInputStream input ;
    DataOutputStream output;

    public  boolean isAlive = true;

    private static volatile ArrayList<ClSv> clientsOnServer = new ArrayList<>();
    // Constructor.
    public Server()
    {
        try{

                serverSocket = new ServerSocket(portNum);
                System.out.println("Server started..waiting for the connection at port: \n"+portNum);

        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void awaitingConnections()
    {
        while(true)
        {
            try
            {
                Socket newClient = serverSocket.accept();
                System.out.print("Client connected....");

                //create a new clientOnServer thread and start it.
                ClSv cl = new ClSv(newClient,clientsOnServer);
                cl.start();
                clientsOnServer.add(cl);

            }
            catch (Exception e)
            {
                System.out.print(e);
            }

        }
    }
    
    public static void main(String args[])
    {

        Server s = new Server();
        s.awaitingConnections();

    }
    
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
