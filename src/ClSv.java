import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by alfred on 2017/03/31.
 */
public class ClSv extends Thread{
    DataInputStream input ;
    DataOutputStream output;
    Socket clientSocket = null;
    String name;
    private ArrayList<ClSv> otherClients = new ArrayList<>();

    public ClSv(Socket clientSocket,ArrayList<ClSv> clSvArrayList)
    {
        this.clientSocket = clientSocket;
        this.otherClients = clSvArrayList;
     //   this.name = username;
    }

    public String getUsername()
    {
        return this.name;
    }
    public void run()
    {
        String message;
        ArrayList<ClSv> otherClients  = this.otherClients;
        try
        {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
            synchronized (this){
                // tell other threads that a new user joined the chatroom.
                for (int i=0;i<otherClients.size();i++) {
                    if (otherClients.get(i)!=this){
                        otherClients.get(i).output.writeUTF("A new client has joined the chatroom");
                    }
                }
            }
            //sending messages
            while (true)
            {
                message = input.readUTF(); //read from current thread
                synchronized (this) {
                    //send the message to all.
                    for (int i = 0; i < otherClients.size(); i++) {
                       otherClients.get(i).output.writeUTF(message);
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.print(e);
        }


    }
}
