package src;
import java.io.*;
import java.net.*;

public class Host {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket, receiveSocket;
	int clientPort = 23;
	int serverPort = 69;
	
	public Host(){
		try {
	      sendReceiveSocket = new DatagramSocket();
	      receiveSocket = new DatagramSocket(clientPort, InetAddress.getLocalHost());
	    } catch (SocketException se) {   // Can't create the socket.
	       se.printStackTrace();
	       System.exit(1);
	    }catch(UnknownHostException ue){
	    	ue.printStackTrace();
		    System.exit(1);
	    }
	}
	
	public void running(){
		byte data[] = new byte[100];
	    receivePacket = new DatagramPacket(data, data.length);
	    
	    //receive the packet from the client
	    try {
	        receiveSocket.receive(receivePacket); 
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	      InetAddress clientAddr = receivePacket.getAddress();
	      int port = receivePacket.getPort();

	      System.out.println("Intermediate Host: Packet received from client(byte): " + receivePacket.getData());
	      System.out.print("Containing(string): ");
	      String received = new String(data,0,receivePacket.getLength());   
	      System.out.println(received);
	      
	      //send the request to the server
	      try {
		    sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
		                                          InetAddress.getLocalHost(), serverPort);
		  } catch (UnknownHostException e) {
		     e.printStackTrace();
		     System.exit(1);
		  }
	      
		  try {
			sendReceiveSocket.send(sendPacket);
		  } catch (IOException e) {
			  e.printStackTrace();
			 System.exit(1);
		  }
	      System.out.println("Intermediate Host: Packet sent to server.");
	      System.out.println();
	      
		  //receive the response from the server
		  data = new byte[100];
		  receivePacket = new DatagramPacket(data, data.length);

		  try {
		     sendReceiveSocket.receive(receivePacket);
		  } catch(IOException e) {
		      e.printStackTrace();
		      System.exit(1);
		  }

		  System.out.println("Get response from the server: ");
		  System.out.println("Server: Packet received(byte): " + receivePacket.getData());
		  System.out.print("Containing(string): ");
		  for(int i = 0 ; i < 4; i++){
			  System.out.print(data[i]);
		  }
	      System.out.println();
	      
		  //send the response to the client
          sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
			                                          clientAddr, port);
		  DatagramSocket sendSocket;
		  try{
			sendSocket = new DatagramSocket();
		    sendSocket.send(sendPacket);
			sendSocket.close(); //Closes the DatagramSocket used to send to the host that sent the request 
	      }catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
	      }		
	      System.out.println("Intermediate Host: Packet sent to client.");
	      System.out.println();
	}
	
	public void close(){
		sendReceiveSocket.close();
		receiveSocket.close();
		System.out.println("The sockets have been closed.");
	}
	
	public static void main(String[] args){
		Host host = new Host();
		while(true){
			host.running();
		}
	}
}