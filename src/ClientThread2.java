/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Alfred
 */
public class ClientThread2 extends Thread{
    private Socket client;
    private DataOutputStream out ;
    private DataInputStream inp ;
    public static boolean alive =true;
    String username;

    public ClientThread2(String host,int port, String username) throws Exception{
        //establish a new connection. and initialise streams.
        this.client = new Socket(host,port);
        OutputStream outToServer = client.getOutputStream();
        out = new DataOutputStream(outToServer);
        inp = new DataInputStream(client.getInputStream());
        this.username = username;

    }

    //thread to monitor client's input stream.
    public void run() {

        try {
            String line;
            while(true){
                line=inp.readUTF(); //read text from input stream/from server..

                if(line=="quit"){  //the server must return "quit" to close connection.
                    break;
                }
                else{
                    //print out the line/message.
                    System.out.println(line);
                }
            }
            alive=false;  //used to notify the writing thread that the server ended the connection.
            this.closeConnection();
        } catch(Exception e){
            System.out.println(e);
        }
    }
    public void writeToServer()throws Exception{
        String message ;
        Scanner s = new Scanner(System.in);
        while (alive){

//            System.out.println("Enter a message:");
            message = s.nextLine();
            try{
                out.writeUTF(username + ": " + message);

            }
            catch (Exception e){
                System.out.println(e);
            }
        }

    }
    public void closeConnection() throws Exception{
        client.close();
        out.close();
        inp.close();
    }


    public static void main(String [] args) throws Exception{
//        String serverName = args[0];
        //String username = args[1];
        String username = "NWTDAN002";
        String serverName = "localhost";
//      int port = Integer.parseInt(args[1]);
        int port = 5678;
        ClientThread d = new ClientThread(serverName,port, username);
        d.start();
        d.writeToServer();
    }

}
