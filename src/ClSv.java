
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
            
            synchronized (this){
                // tell other threads that a new user joined the chatroom.
                for (int i=0;i<otherClients.size();i++) {
                    if (otherClients.get(i)!=this){
                        otherClients.get(i).output.writeUTF("Client " +this.username+ " has joined the chatroom");
                    }
                }
            }
            //sending messages
            while (true)
            {
                message = input.readUTF(); //read from current thread
                if (message.equals("/quit")){
                    for (ClSv serv : otherClients) {
                        if (serv!=this) {
                            serv.output.writeUTF("Client " + this.username + " has left the chatroom.");
                        }
                        if (serv==this) {
                            
                            otherClients.remove(serv);
                        }
                    }
                    break;
                }
                //Code for receiving images
                if (message.startsWith("/send")) {
                    //String filename = message.substring(6);
                    String extension = input.readUTF();
                    int imageSize = input.readInt();
                    
                    byte[] imageArray = new byte[imageSize];
                    input.readFully(imageArray);
                    imageArrays.add(imageArray);
                    for (ClSv serv : otherClients) {
                        if (serv!=this) {
                            serv.output.writeUTF(this.username+ " has sent an image of "+imageSize/1024+ " KB. Enter /download to accept it.");
                        }
                    }
                    
                    
                }else if (message.startsWith("/download")) {
                    
                } else {
                    currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
                    timeString = "<"+currentTime.toString()+">";
                    synchronized (this) {
                        //send the message to all.
                        for (int i = 0; i < otherClients.size(); i++) {
                            otherClients.get(i).output.writeUTF(timeString+" "+message);
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
