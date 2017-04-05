/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

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
    int dlCounter =1;

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

                if (line.startsWith("/download")) {         //1: get download message
                    String ex = inp.readUTF();              //2: get file extension
                    int imgSize = inp.readInt();            //3: Get image size
                    byte[] imageArray = new byte[imgSize];
                    inp.readFully(imageArray);              //4: Get byte array
                    //System.out.println("bytes received");

                    //convert byte array to image
                    InputStream in = new ByteArrayInputStream(imageArray);
                    BufferedImage convertImage = ImageIO.read(in);
                    ImageIO.write(convertImage, ex, new File(username + Integer.toString(dlCounter) + "." + ex));
                    dlCounter++;
                    history.add("Image downloaded.");
                } else {
                    history.add(line);
                }
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
            //code for sending images
            if (message.startsWith("/send")) {
                                          
                String filename = message.substring(6);
                int ex = filename.indexOf(".");
                ex++;
                String extension = filename.substring(ex);
                //convert image to byte array
                BufferedImage img = ImageIO.read(new File(filename));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, extension, baos);
                baos.flush();
                byte[] imageBytes = baos.toByteArray();
                baos.close();
                int imageSize = imageBytes.length;
                
                //System.out.println(imageSize);
                
                out.writeUTF(message);                                      //1: Send message
                out.writeUTF(extension);                                    //2: Send file extension
                out.writeInt(imageSize);                                    //3: Send image size
                out.write(imageBytes, 0, imageSize);                        //4: send byte array

                Server.clearScreen();
                for (String preMessage : history) {
                    System.out.println(preMessage);
                }
                System.out.println("Enter a message: ");

                
            }else if (message.startsWith("/download")) {
                out.writeUTF(message);
                Server.clearScreen();
                for (String preMessage : history) {
                    System.out.println(preMessage);
                }
                System.out.println("Enter a message: ");

            } else if(!message.equals("")) try{
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


    public static void main(String [] args) throws Exception
    {
          String serverName = args[0];
          String username = args[1];
    //    String username = "admin";
     //   String serverName = "localhost";
          int port = Integer.parseInt(args[2]);
      //  int port = 5678;
        ClientThread d = new ClientThread(serverName,port, username);
        String welcome = "You (" + username + ") have successfully joined the chatroom.";
        d.history.add(welcome);
        Server.clearScreen();
        System.out.println(welcome);
        d.start();
        d.writeToServer();
    }
  
}
