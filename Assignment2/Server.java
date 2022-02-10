package Assignment2;
import java.io.*;
import java.net.*;

public class Server {

   DatagramPacket sendPacket, receivePacket;
   DatagramSocket sendSocket, receiveSocket;

   public Server()
   {
      try {
         sendSocket = new DatagramSocket();
         receiveSocket = new DatagramSocket(5000);
         receiveSocket.setSoTimeout(2000);
      } catch (SocketException se) {
         se.printStackTrace();
         System.exit(1);
      } 
   }

   public void receiveAndEcho()
   {
      byte data[] = new byte[100];
      receivePacket = new DatagramPacket(data, data.length);
      System.out.println("Server: Waiting for Packet.\n");

      try {        
         System.out.println("Waiting..."); 
         receiveSocket.receive(receivePacket);
      } catch (IOException e) {
         System.out.print("IO Exception: likely:");
         System.out.println("Receive Socket Timed Out.\n" + e);
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Server: Packet received:");
      System.out.println("From host: " + receivePacket.getAddress());
      System.out.println("Host port: " + receivePacket.getPort());
      int len = receivePacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: " );

      String received = new String(data,0,len);   
      System.out.println(received + "\n");
      
      try {
          Thread.sleep(5000);
      } catch (InterruptedException e ) {
          e.printStackTrace();
          System.exit(1);
      }

      sendPacket = new DatagramPacket(data, receivePacket.getLength(),
                               receivePacket.getAddress(), receivePacket.getPort());

      System.out.println( "Server: Sending packet:");
      System.out.println("To host: " + sendPacket.getAddress());
      System.out.println("Destination host port: " + sendPacket.getPort());
      len = sendPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      System.out.println(new String(sendPacket.getData(),0,len));

      try {
         sendSocket.send(sendPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Server: packet sent");
      sendSocket.close();
      receiveSocket.close();
   }

   public static void main( String args[] )
   {
      Server c = new Server();
      c.receiveAndEcho();
   }
}
