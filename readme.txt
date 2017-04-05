Group Members:
Jonathan Leyland - LYLJON002
Alfred Mothapo   - MTHMAS016
Adam Meier 	 - MRXADA002

-----------------------------

How to run the program

Compile all the files by running "make all"
The classes have now been compiled to the bin folder

1) Run the server by calling:
Java -cp bin Server <portNumber> 
where portNumber is greater than 1023

2) (IN A NEW TERMINAL) Run a client by calling:
Java -cp bin ClientThread <serverName> <username> <portNumber>
Where serverName is the IP address of the server. If you are running the client on the same computer as the server then you can use "localhost" as the serverName. Please note that using the ip address of the server only works if the server is running on windows, but it can be connected to from an Ubuntu client. 
Username can be anything you wish as long as it is one word.
portNumber should be the same port number used to start the Server 

3) You are able to add additional clients by running the previous step again (in a new terminal)

-----------------------------------

Functions that a client can perform

/<username> <message> : sends a private message to another user in the chatroom.
/send <imageName>     : request other clients in the chatroom to download an image. Image must exist in images folder. Remember the type extension (e.g. sponge.jpg)
/download 	      : downloads the most recent image that has been sent to the chatroom. It is saved to images folder with name <username> + the number of the 				download. 
/quit		      : disconnects the client from the chatroom and ends the session for that client.

-----------------------------------

To exit the server: use control + Z to kill the process. 
To clear the bin folder, call "make clean"
