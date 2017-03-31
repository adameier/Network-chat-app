
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

    private ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<>();
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

    public void closeConnection(){
    
        try{
            input.close();
            output.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void sendToClients(String message)
    {
        for (int j = 0; j<clientOutputStreams.size(); j++)
        {
            try
            {
                clientOutputStreams.get(j).writeUTF(message);
            }
            catch(Exception e)
            {
                System.out.print(e);
            }

        }
    }

    public void run()
    {
        String message;

        try
        {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            clientOutputStreams.add(output);
            message = input.readUTF();

            while (!message.equals("quit"))
            {
                message = input.readUTF();
                sendToClients(message);
                System.out.print(message);
            }
        }
        catch(Exception e)
        {
            System.out.print(e);
        }

        sendToClients("Client disconnected...");



    }

    public static void main(String args[])
    {

        Server s = new Server();
        s.awaitingConnections();

    }

    public void awaitingConnections()
    {
        while(isAlive)
        {
            try
            {
                Socket newClient = serverSocket.accept();
                System.out.print("Client connected....");
                Server server = new Server();
                server.clientSocket = newClient;
                Thread serverThread = server;
                serverThread.start();
            }
            catch (Exception e)
            {
                System.out.print(e);
            }



        }
    }

}
