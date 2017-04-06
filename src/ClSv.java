
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Created by alfred on 2017/03/31.
 */
public class ClSv extends Thread{
    DataInputStream input ;
    DataOutputStream output;
    Socket clientSocket = null;
    String username;
    private volatile ArrayList<ClSv> otherClients = new ArrayList<>();
    private volatile ArrayList<byte[]> imageArrays = new ArrayList<>();
    byte[] imageData;
    String imgExtension;
    LocalTime currentTime;

    public ClSv(Socket clientSocket,ArrayList<ClSv> clSvArrayList, ArrayList<byte[]> images)
    {
        this.clientSocket = clientSocket;
        this.otherClients = clSvArrayList;
        this.imageArrays = images;
        try { 
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            username = input.readUTF();
        } catch (Exception e) {
            System.out.println(e);
        }
        
     //   this.name = username;
    }

    public String getUsername()
    {
        return this.username;
    }
    
    public void closeConnection() throws Exception{
        
        output.close();
        input.close();
        //client.close();
    }
    
    public void run()
    {
        String message;
        String timeString;
        ArrayList<ClSv> otherClients  = this.otherClients;
        try
        {
                // tell other threads that a new user joined the chatroom.
                for (ClSv serv : otherClients) {
                    if (serv!=this){
                        synchronized (serv) {
                            serv.output.writeUTF("Client " +this.username+ " has joined the chatroom");
                        }
                    }
                }

            //sending messages
            while (true)
            {
                message = input.readUTF();                  //1: get message
                if (message.equals("/quit")){
                    for (ClSv serv : otherClients) {
                        if (serv!=this) {
                            synchronized (serv) {
                                serv.output.writeUTF("Client " + this.username + " has left the chatroom.");
                            }
                        }
                        if (serv==this) {
                            
                            otherClients.remove(serv);
                        }
                    }
                    break;
                }
                //Code for receiving images
                if (message.startsWith("/send")) {
                    String filename = "images/" + message.substring(6);
                    String extension = input.readUTF();             //2: get extension
                    int imageSize = input.readInt();                //3: get image size
                    
                    byte[] imageArray = new byte[imageSize];
                    input.readFully(imageArray);                    //4: get byte array
                    imageArrays.add(imageArray);
                    for (ClSv serv : otherClients) {
                        if (serv!=this)
                        {
                            serv.imgExtension = extension;
                            serv.imageData = imageArray;
                            synchronized (serv) {
                                serv.output.writeUTF(this.username+ " has sent an image of "+imageArray.length/1024+ " KB. Enter /download to accept it.");
                            }
                        }
                        else
                        {
                        	this.output.writeUTF("You have successfully sent a request for other clients to download your image.");
                        }
                    }
                    
                    
                }else if (message.startsWith("/download")) {
                    synchronized (this) {
                        System.out.println(imgExtension);
                        output.writeUTF(message);                   //1: send download message
                        output.writeUTF(imgExtension);              //2: send extension
                        int imgSize = imageData.length;
                        output.writeInt(imgSize);                   //3: send image size
                        output.write(imageData, 0, imgSize);        //4: send byte array

                    }
                } else if (message.contains("/")) {
                    for (ClSv serv : otherClients) {
                        if (message.contains("/"+serv.username)) {
                            String[] messageContent = message.split("/"+serv.username+" ");
                            String sender = "[pm-"+serv.username+"] " + messageContent[0]+messageContent[1];
                            String receiver = "[pm] " + messageContent[0]+messageContent[1];

                            currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
                            timeString = "<"+currentTime.toString()+">";

                            serv.output.writeUTF(timeString+" "+receiver);
                            this.output.writeUTF(timeString+" "+sender);
                        }

                    }

                } else {
                    currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
                    timeString = "<"+currentTime.toString()+">";

                        //send the message to all.
                        for (ClSv serv : otherClients) {
                            synchronized (serv) {

                                    serv.output.writeUTF(timeString+" "+message);
                            }
                        }

                }
                
            }
        }
        
        catch(Exception e)
        {
            //System.out.print(e);
        }
        try {
            closeConnection();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        


    }
}
