
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
public class Server {
    
    ServerSocket serverSocket;
    Socket clientSocket = null;
    DataInputStream input ;
    DataOutputStream output;
    public  boolean isAlive=Client.isAlive;
    private ArrayList<Socket> clientSockets = new ArrayList<>();
    // Constructor.
    public Server(int portNum){
        try{
            serverSocket = new ServerSocket(portNum);
            System.out.println("Server started..waiting for the connection at port: \n"+portNum);
            while (clientSockets.size()!=2){
                clientSocket = serverSocket.accept(); //waiting for incoming connections.
                clientSockets.add(clientSocket);
                System.out.print("Client "+clientSockets.size()+" joined the chatroom\n");
                input = new DataInputStream(clientSocket.getInputStream());
                output = new DataOutputStream(clientSocket.getOutputStream());
            }

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
    public void readFromClients(){
            try {

                while (isAlive){

                    String message2 = new DataInputStream(clientSockets.get(0).getInputStream()).readUTF();
                    String message = input.readUTF();
                    if (message=="quit"){

                        Client.isAlive=false;

                    }
                    else{

                        System.out.println("The client 2 says: "+message);
                        output.writeUTF("The sever says: "+message);
                        new DataOutputStream(clientSocket.getOutputStream()).writeUTF(message2);
                        System.out.println("The client 1 says: "+message2);
                    }

                }
            }
            catch (Exception e){
                System.out.println(e);
            }

    }

}
