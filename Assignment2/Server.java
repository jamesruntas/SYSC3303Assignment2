package Assignment2;
import java.io.*;
import java.net.*;

public class Server {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket receiveSocket;
	int serverPort = 69;
	byte message[] = new byte[100];
	String filename = "";
	
	public Server(){
		try {
	       receiveSocket = new DatagramSocket(serverPort, InetAddress.getLocalHost());
	    }catch (SocketException se) {   // Can't create the socket.
	        se.printStackTrace();
	        System.exit(1);
	    }catch (UnknownHostException ue){ // Can't find the host.
	    	ue.printStackTrace();
	    	System.exit(1);
	    }
	}
	
	public void running(){
	   byte data[] = new byte[100];
	   receivePacket = new DatagramPacket(data, data.length);
	   System.out.println("Server: Waiting for Packet.\n");
	   
	   //receive the request from the Intermediate Host
	   try {        
	     System.out.println("Waiting..."); // so we know we're waiting
	     receiveSocket.receive(receivePacket);
	   } catch (IOException e) {
	      System.out.print("IO Exception: likely:");
	      System.out.println("Receive Socket Timed Out.\n" + e);
	      e.printStackTrace();
	      System.exit(1);
	   }
	   
	   //check the request whether or not is valid
	   boolean valid = checkValid(data, data.length);
	   if(!valid){
		   System.out.println("This request is invalid. Server Terminates!");
		   throw new IllegalArgumentException("Invalid Request Exception");
	   }

       System.out.println("Packet received:");
	   System.out.println("Server: Request(byte): " + receivePacket.getData());
	   System.out.print("Containing(String) : " );
	   // Form a String from the byte array.
	   String received = new String(data,0,receivePacket.getLength());   
	   System.out.println(received + "\n");
	   
	   if(data[1] == 49){
		   message = new byte[]{0,3,0,1}; 
	   }
	   else if(data[1] == 50){
		   message = new byte[]{0,4,0,0};
	   }
	   
	   sendPacket = new DatagramPacket(message, message.length,
               receivePacket.getAddress(), receivePacket.getPort());
	   
	   DatagramSocket sendSocket;
	   //send the response to the Intermediate Host
	   try{
		  sendSocket = new DatagramSocket();
		  sendSocket.send(sendPacket);
		  sendSocket.close(); //closes the DatagramSocket
	   }catch(IOException e){
	      e.printStackTrace();
	      System.exit(1);
	   }
	   
	   System.out.println("Server: Sending response(byte): " + message);
	   System.out.print("Containing(String): ");
	   for (byte d : message) {
			System.out.print(d);
		}
	   System.out.println();
	   
	   System.out.println("Server: packet sent");
	   System.out.println();
	}
	
	public boolean checkValid(byte[] data, int length){
		String mode = "";
		
		if(data[0] != 48){ //(byte) 0 = (integer) 48
			return false;
		}		  		
		if(data[1] != 49  && data[1] != 50){ //(byte) 1 = (integer) 49 and (byte) 2 = (integer) 50
			return false;
		}

		int i = 2;
		while(data[i] != 48 && i < length-1){
			filename += (char)data[i++];
		}	
		
		if(filename == ""){
			return false;
		}
		
		if(data[i++] != 48){
			return false;
		}
		
		while(data[i] != 48 && i < length-1){
			mode += (char)data[i++]; 
		}		
		
		mode = mode.toLowerCase();
		if(!mode.equals("netascii") && !mode.equals("octet")){
			return false;
		}

		if(data[i++] != 48){
			return false;
		} 
		
		while (i < length ) {
			if (data[i++] != 0) {
				return false;
			}
		}	
		
		return true;
	}
	
	public void close(){
		receiveSocket.close();
		System.out.println("The socket has been closed.");
	}
	
	public static void main(String[] args){
		Server s = new Server();
		while(true){
			s.running();
		}
	}
}