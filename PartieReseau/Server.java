package chat;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/*
 * @Author Morgan Valentin
 * @Source code used to understand => https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
*/

public class Server  { 	
	protected static int num=0;//Used for user to add 5 (methode add5) to this num variable. (Testing purposes)
	
	//Colours on ouput => ONLY WORKS ON LINUX (maybe mac ?) NOT WINDOWS
	protected static final String red    = "\u001B[31m";
	protected static final String blue   = "\u001B[34m";
	protected static final String reset  = "\u001B[0m";
	protected static final String purple = "\u001B[35m";
	protected static final String yellow = "\u001B[33m";
	protected static final String white  = "\u001B[37m";
	
	protected static HashMap<Long,String> idAndNames = new HashMap<>();
	
<<<<<<< HEAD
=======
	
	//----------------------------------
	public static boolean checkNameInHashmapServer(Scanner scn,String userNameInput,HashMap<Long,String> map) {
		System.out.println("enter a name");
		userNameInput = scn.nextLine();
		if (userNameInput.length()==0) {
			while (userNameInput.length()==0) {
				System.out.println("Re-enter a name");
				userNameInput = scn.nextLine();
				if (map.containsValue(userNameInput)) {
					System.out.println(userNameInput+" is in hashMap");
					return true;
				}
				else {
					System.out.println(userNameInput+" is NOT in hashMap");
					return false;
				}
			}
		}
		if (map.containsValue(userNameInput)) {
			System.out.println(userNameInput+" is in hashMap");
			return true;
		}
		else {
			System.out.println(userNameInput+" is NOT in hashMap");
			return false;
		}
	}
	//--------------------------------------------
>>>>>>> 6e17e1bf683c5c1903e19bc0782f467adc430dd8
	
    public static void main(String[] args) throws IOException, InputMismatchException  { 
    	//Selecting port number from user to use => Plus tard par interface graphique
    	//Attention => Le port du client devra etre identique sinon il ne "verra" pas le server !!
    	try {
    		Scanner userInput = new Scanner(System.in);//Get port number from user input
        	System.out.println(purple+"Port number ?"+reset);
    		int port = userInput.nextInt();
            ServerSocket servSock = new ServerSocket(port);//Creating a new serverSocket with port given by user. 
            
            System.out.println(red+"Server started !"+reset);
            System.out.println("Waiting for player(s) to connect");
              
            // Infinite loop => Waiting that a client connects {accept() method}
            
            while (true)  { 
                Socket sock = null; //Socket for client communication => "Active" socket
                  
                try { 
                    // socket object to receive incoming client requests => "Passive" socket
                    sock = servSock.accept();
                      
                    // In and out streams => Information received (inputStream) and sent (outputStream) :
                    DataInputStream in = new DataInputStream(sock.getInputStream()); 
                    DataOutputStream out = new DataOutputStream(sock.getOutputStream()); 
                    
                    // create a new thread object => Nouveau "processus" en quelque sorte
                    Thread t = new ClientHandler(sock, in, out); 
      
                    // Demarre le processus t (gestion des clients) et mais en "pause" la suite des instructions du server.
                    t.start(); //Start method search the run() method from the Threat class heritence 
                } 
                catch (IOException e){ 
                	servSock.close();
                	userInput.close();
                	sock.close();
                    e.printStackTrace();
                }
            }
    	}
    	catch(InputMismatchException a) {
        	System.out.println(Server.red+"FATAL ERROR :"+Server.reset+Server.purple+" IP and Port must be Integer"+Server.reset);
        }	 
    } 
}
//To "create" a new thread (process ?) either implement interface Runnable OR inherite from the "Thread" class
class ClientHandler extends Thread  
{ 
    DateFormat myDateFormat = new SimpleDateFormat("EEEE,dd-MMMM-YYYY");//Used for user to ask for Date
    DateFormat myTimeFormat = new SimpleDateFormat("HH:mm:ss");//Used for user to ask for Time
    final DataInputStream in; 
    final DataOutputStream out; 
    final Socket sock; 
    private static int numberOfClientsConnected=0;
  
    // Constructor 
    public ClientHandler(Socket sock, DataInputStream in, DataOutputStream out)  
    { 
        this.sock = sock;
        numberOfClientsConnected++;
        this.in = in; 
        this.out = out;
        testConnection();//Tests if there's 0 client OR More than 2 connected => If so Closes ressources and Exit program.
        
        /* Tester avec 3 clients :
         * Des que le 3eme client se connecte, il recoit toute une serie d erreur (a catcher et masquer de l utilisateur)
         * Cependant, le server se couppe correctement.
         * Les 2 autres clients (client 1 et 2) ne voit pas de message disant que la connection est couper ==>>Optimiser
         * Mais une fois une commande entrer => Erreur disant que le serveur n'est plus joignable.
         */
    } 
  
    @Override
    public void run()  { 
        String receivedFromClient; 
        String sendToClient;
        final String name = Thread.currentThread().getName();
        final long id = Thread.currentThread().getId(); 
        Server.idAndNames.put(id, name);//Add id and thread name to hashMap
        String userName = Server.idAndNames.get(id);//userName = valeur de la cle id (nom du process ex: thread-0)
        
        //---------------------
        //Choppe le nom du client (que le client envoie au serveur)
        try {       
        	userName = in.readUTF();//Stocke le nom du client dans le hashMap a la cle (id du Thread) (valeur=>userName)
        	
        } catch (IOException e) { 
            System.out.println("Erreur");
        }
        System.out.println("A new "+Server.purple+"client"+Server.blue+" \""+userName+"\""+Server.reset+" with id" +Server.red+" ("+id+")"+Server.reset+" joined via " +Server.yellow+ sock.getLocalAddress().toString().replaceAll("/", "")+Server.reset);//To see which ip the client used to connect 
        System.out.println("-----------------------------------------");
        //--------------------
        
        while (true)  { 
            try { 
  
                // Show the different possibilities (options) available
                out.writeUTF("Welcome "+Server.blue+"\""+userName+"\""+Server.reset+" your id is : "+Server.red+id+Server.reset+"\n"+
                		"What do you want? Options =>"+Server.purple+"[Date | Time | add5 | name | con(showNumOfClientConnected)]"+Server.reset+"\n"+ 
                            "Type "+Server.red+"'end'"+Server.reset+" to terminate connection.\n"
                		+"---------------------------"); 
                  
                // receive the answer from client 
                receivedFromClient = in.readUTF(); 
                  
                if(receivedFromClient.equals("end")) {
                	System.out.println(Server.blue+userName+"> "+Server.red+receivedFromClient+Server.reset);
                    System.out.println("Client "+Server.blue+"\""+userName+"\""+Server.reset+" with id n°"+Server.blue+id+" "+Server.yellow+"(ip= "+this.sock.getInetAddress().toString().replaceAll("/", "") +")"+Server.reset+ " sends "+Server.red+"end command..."+Server.reset); 
                    System.out.println("Bye bye "+Server.blue+"\""+userName+"\""+Server.reset); 
                    this.sock.close();
                    numberOfClientsConnected--;
                    System.out.println(Server.red+"Connection closed"+Server.reset); 
                    break; 
                } 
                  
                  
                // write on output stream based on the 
                // answer from the client 
                switch (receivedFromClient) { 
                //The "case" correspond to what the client sent (if none of the command => default : "invalid output"
                  
                    case "Date" : 
                    	//Shows the date
                    	Date date = new Date();
                    	sendToClient = myDateFormat.format(date); 
                        out.writeUTF(Server.yellow+"Server>"+Server.white+" "+sendToClient+Server.reset+"\n"+"-------------------");
                        System.out.println(Server.blue+userName+"> "+Server.red+receivedFromClient+Server.reset);//Visualisation des donnees demande par le client
                        break; 
                          
                    case "Time" : 
                    	//Shows the Time
                    	Date date2 = new Date();//Why here ? => Because Time changes ...
                    	sendToClient = myTimeFormat.format(date2); 
                        out.writeUTF(Server.yellow+"Server>"+Server.white+" "+sendToClient+Server.reset+"\n"+"-------------------"); 
                        System.out.println(Server.blue+userName+"> "+Server.red+receivedFromClient+Server.reset);//Visualisation des donnees demande par le client
                        break;
                        
                    case "add5" : 
                    	
                    	//Adds 5 to the "num" variable and displays its value
                        Server.num += 5;
                        out.writeUTF(Server.yellow+"Server>"+Server.white+" Num = "+Server.num+Server.reset+"\n"+"-------------------");
                        System.out.println(Server.blue+userName+"> "+Server.red+receivedFromClient+Server.reset);//Visualisation des donnees demande par le client
                        System.out.println("Value of num : "+Server.num);
                        break; 
                        
                    case "con" : 
                    	//Show how many clients are connected to the server
                        out.writeUTF(Server.yellow+userName+">"+Server.white+" Number of connections : "+numberOfClientsConnected+Server.reset+"\n"+"-------------------"); 
                        System.out.println(Server.blue+userName+"> "+Server.red+receivedFromClient+Server.reset);//Visualisation des donnees demande par le client
                        System.out.println("Num of connec : "+numberOfClientsConnected);
                        break;
                        
                    case "name" : //What's my name ? => Answers that...
                    	System.out.println(Server.blue+userName+"> "+Server.red+receivedFromClient+Server.reset);
                    	receivedFromClient = in.readUTF();//Receives new name from client
<<<<<<< HEAD
                        userName = receivedFromClient;
                        Server.idAndNames.replace(id, userName);
                        out.writeUTF(Server.yellow+"Server>"+Server.white+" Name changed to "+"\""+Server.red+userName+Server.white+"\""+Server.reset+"\n"+"-------------------"); 
=======
                    	
                    	//Checker nom client si deja dans hashmap-----
                    	
                    	//---------------------------------------
                    	
                        userName = receivedFromClient;
                        Server.idAndNames.replace(id, userName);
                        out.writeUTF(Server.yellow+"Server>"+Server.white+" Name changed to "+"\""+Server.red+userName+Server.white+"\""+Server.reset+"\n"+"-------------------"); 
                        
>>>>>>> 6e17e1bf683c5c1903e19bc0782f467adc430dd8
                        System.out.println(Server.white+"Name changed to => "+Server.blue+userName+Server.reset);
                        break;
                    	
                    default: 
                        out.writeUTF("Invalid input");
                        break; 
                } 
            } catch (IOException e) { 
                //e.printStackTrace();
            	System.out.println(Server.red+"Et on ne dit même pas au revoir ?"+Server.reset+Server.blue+" Je vois..."+Server.reset);
            	System.exit(0);
            }
        } 
          
        testConnection();//Tests if there's 0 client OR More than 2 connected => If so Closes ressources and Exit program.
    }
    public void testConnection() {
    	try { 
            // closing resources 
            if (numberOfClientsConnected==0) {
            	System.out.println("No more clients..."+Server.red+"ENDING Server!"+Server.reset);
            	this.in.close(); 
                this.out.close(); 
                this.sock.close();
            	System.exit(0);//Exit program => Thread stop
            }
            if (numberOfClientsConnected>2) {
            	System.out.println("More than 2 clients... ENDING Server!");
            	this.in.close(); 
                this.out.close(); 
                this.sock.close();
            	System.exit(0);
            }
              
        }catch(IOException e){ 
            e.printStackTrace();
        }
    }
} 
  
