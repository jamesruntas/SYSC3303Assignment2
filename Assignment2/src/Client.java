package src;
import java.io.*;
import java.net.*;

public class Client {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	int port = 23;
	String filename = "file.txt";
	String mode = "octet";
	
	public Client(){
		try {
	      sendReceiveSocket = new DatagramSocket();
	    } catch (SocketException se) {   // Can't create the socket.
	    	se.printStackTrace();
	        System.exit(1);
	    }
	}
	
	public void request(int i){
		String s = "";
		String request = "";
		
		// create the request
		if(i % 2 == 0){
			s += "01";
			s += filename;
			s += "0";
			s += mode;
			s += "0";
			request = "Read";
		}
		else if(i == 11){
			s += "04";
			s += filename;
			s += "1";
			s += mode;
			s += "345";
			request = "Invalid";
		}
		else{
			s += "02";
			s += filename;
			s += "0";
			s += mode;
			s += "0";
			request = "Write";
		}
		
	    byte msg[] = s.getBytes();
	    
	    //send the request to the server
	    try {
	        sendPacket = new DatagramPacket(msg, msg.length,
	                                          InetAddress.getLocalHost(), port);
	    } catch (UnknownHostException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
	    
	    System.out.println("Client: Sending packet(byte):");
	    System.out.println("Number " + i + " "+ request + " request :  " + msg);
	    System.out.print("Containing(string): ");
	    System.out.println(new String(sendPacket.getData(),0,sendPacket.getLength())); 
        
	    try {
	       sendReceiveSocket.send(sendPacket);
	    } catch (IOException e) {
	       e.printStackTrace();
	       System.exit(1);
	    }
        System.out.println("Client: Packet sent.\n");
	    receive();
	    System.out.println();
	    s = "";
	}
	
	public void receive(){
		byte data[] = new byte[100];
	    receivePacket = new DatagramPacket(data, data.length);
	    
	    //receive the response from the server
	    try {
	        sendReceiveSocket.receive(receivePacket);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	    
	    System.out.println("Client: Packet received(byte): " + receivePacket.getData());
	    System.out.print("Containing(string): ");
		for(int i = 0 ; i < 4; i++){
			System.out.print(data[i]);
		}
		System.out.println();
	}
	
	
	public void close(){
		sendReceiveSocket.close();
		System.out.println("Socket has been closed.");
	}
	
	public static void main(String[] args){
		Client c = new Client();
		for(int i = 1; i <= 11; i++){
			c.request(i);
		}
		c.close();
	}
}
