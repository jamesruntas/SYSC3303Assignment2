package src;
import java.io.*;
import java.net.*;

public class Client {

	   DatagramPacket sendPacket, receivePacket;
	   DatagramSocket sendReceiveSocket;

	   public Client()
	   {
	      try {
	         // Construct a datagram socket and bind it to any available 
	         // port on the local host machine. This socket will be used to
	         // send and receive UDP Datagram packets.
	         sendReceiveSocket = new DatagramSocket();
	      } catch (SocketException se) {   // Can't create the socket.
	         se.printStackTrace();
	         System.exit(1);
	      }
	   }

	   public void sendAndReceive()
	   {
	      String s = "Anyone there?";
	      System.out.println("Client: sending a packet containing:\n" + s);

	      byte msg[] = s.getBytes();

	      try {
	         sendPacket = new DatagramPacket(msg, msg.length,
	                                         InetAddress.getLocalHost(), 5000);
	      } catch (UnknownHostException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }

	      System.out.println("Client: Sending packet:");
	      System.out.println("To host: " + sendPacket.getAddress());
	      System.out.println("Destination host port: " + sendPacket.getPort());
	      int len = sendPacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: ");
	      System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"

	      // Send the datagram packet to the server via the send/receive socket. 

	      try {
	         sendReceiveSocket.send(sendPacket);
	      } catch (IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }

	      System.out.println("Client: Packet sent.\n");

	      byte data[] = new byte[100];
	      receivePacket = new DatagramPacket(data, data.length);

	      try {
	         // Block until a datagram is received via sendReceiveSocket.  
	         sendReceiveSocket.receive(receivePacket);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }

	      // Process the received datagram.
	      System.out.println("Client: Packet received:");
	      System.out.println("From host: " + receivePacket.getAddress());
	      System.out.println("Host port: " + receivePacket.getPort());
	      len = receivePacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: ");

	      // Form a String from the byte array.
	      String received = new String(data,0,len);   
	      System.out.println(received);

	      // We're finished, so close the socket.
	      sendReceiveSocket.close();
	   }

	   public static void main(String args[])
	   {
		  System.out.println("Client.java");
	      Client c = new Client();
	      c.sendAndReceive();
	   }
	}
