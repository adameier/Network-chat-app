
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

    /**
     *
     * @author mthmas016
     */
    public class Client2 {
        private Socket clientSocket;
        private DataInputStream input;
        private DataOutputStream output;
        public static boolean isAlive=true;
        //constructor
        public Client2(String machine_name,int Port_num){
            try{

                clientSocket =  new Socket(machine_name,Port_num);
                input = new DataInputStream(clientSocket.getInputStream());
                output = new DataOutputStream(clientSocket.getOutputStream());
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        //close the connection.
        public void closeConnection() {
            try{
                output.close();
                input.close();
                clientSocket.close();
            }
            catch(Exception e){
                System.out.println(e);

            }

        }
        //Send messages.
        public void sendMessage(){
            String message ;
            Scanner s = new Scanner(System.in);
            while (isAlive){

                System.out.println("Enter a message:");
                message = s.nextLine();
                try{
                    output.writeUTF(message);

                }
                catch (Exception e){
                    System.out.println(e);
                }
            }

        }
        public static void main(String[] args){

            Client2 c = new Client2("localhost",8080);
            c.sendMessage();
        }

    }


