/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Alfred
 */
public class ClientThread extends Thread{
    private Socket client;
    private DataOutputStream out ;
    private DataInputStream inp ;
    public static volatile boolean alive =true;
    String username;
    ArrayList<String> history = new ArrayList<>();

    public ClientThread(String host,int port, String username) throws Exception{
        //establish a new connection. and initialise streams.
        this.client = new Socket(host,port);
        OutputStream outToServer = client.getOutputStream();
        out = new DataOutputStream(outToServer);
        inp = new DataInputStream(client.getInputStream());
        this.username = username;
        out.writeUTF(username);

    }

    //thread to monitor client's input stream.
    public void run() {
        
       try {
            String line;
            while(alive){
                System.out.println("Enter a message: ");
                line=inp.readUTF(); //read text from input stream/from server..
                history.add(line);
                Server.clearScreen();
                for (String preMessage : history) {
                    System.out.println(preMessage);
                }
                
                
            }
            //alive=false;  //used to notify the writing thread that the server ended the connection.
            //this.closeConnection();
       } catch(Exception e){
           //System.out.println(e);
       }
   }
    public void writeToServer()throws Exception{
        String message ;
        Scanner s = new Scanner(System.in);
        while (alive){

//            System.out.println("Enter a message:");
            message = s.nextLine();
            if (message.equals("/quit")) {
                break;
            }
            if(!message.equals("")) try{
                out.writeUTF(username + ": " + message);

            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        alive=false;  
        this.closeConnection();
        
    }
    public void closeConnection() throws Exception{
        out.writeUTF("/quit");
        out.close();
        inp.close();
        client.close();
    }


    public static void main(String [] args) throws Exception{
//        String serverName = args[0];
        //String username = args[1];
        String username = "LYLJON002";
        String serverName = "localhost";
//      int port = Integer.parseInt(args[1]);
        int port = 5678;
        ClientThread d = new ClientThread(serverName,port, username);
        String welcome = "You (" + username + ") have successfully joined the chatroom.";
        d.history.add(welcome);
        System.out.println(welcome);
        d.start();
        d.writeToServer();
    }
  
}
