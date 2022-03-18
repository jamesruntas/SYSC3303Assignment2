package src;
import java.io.IOException;
import java.net.*;
import java.util.*;


public class Host implements Runnable {
    private Queue<DatagramPacket> queue;
    int portNumber;
    private DatagramSocket recieveSocket;
    private DatagramSocket socket;
    private InetAddress local;
    private DatagramPacket recievedPacket;
    private DatagramPacket ackPacket;


    public Host(int portNumber) {
        queue = new LinkedList<DatagramPacket>();
        this.portNumber = portNumber;
    }

    public void run() {
        try {
            recieveSocket = new DatagramSocket(portNumber); //creates a socket bound to port portNumber
            System.out.println(Thread.currentThread().getName() + " is running on port: " + portNumber);
            local = InetAddress.getLocalHost(); //Creates inetaddress containing localhost
            byte[] ackData = "ack".getBytes(); //Defines ack byte array
            byte[] negAck = "NA".getBytes();;
            recievedPacket = new DatagramPacket(new byte[2000], 2000); //create the packet to recieve into

            while (true) { //loop infinitely
                recievedPacket = new DatagramPacket(new byte[2000], 2000);
                recieveSocket.receive(recievedPacket);//Recieve a packet
//				printPacket(recievedPacket, false);
                if (new String(recievedPacket.getData()).trim().equals("request")) { //If the recievedPacket was a request
                    if (queue.isEmpty()) { //If there are no packets to forward
                        ackPacket = new DatagramPacket(negAck, negAck.length, local, recievedPacket.getPort()); //acknowledge that packet
//						printPacket(ackPacket, true);
                        recieveSocket.send(ackPacket);//acknowledge that packet
                    } else {
                        System.out.println(Thread.currentThread().getName()+": Request Receieved, there are " + queue.size()+ "messages waiting");
//						printPacket(queue.peek(), true);
                        recieveSocket.send(queue.remove()); //Send the first packet waiting

                    }
                } else { //if the recievedPacket was not a request, it must have been data
                    ackPacket = new DatagramPacket(ackData, ackData.length, local, recievedPacket.getPort()); //acknowledge that packet
//					printPacket(ackPacket, true);
                    recieveSocket.send(ackPacket);//acknowledge that packet

                    if (recievedPacket.getPort() == 69) { //If the data came from the server, it must be going to client
                        recievedPacket.setPort(22); //Set the packet's port to the client port
                    } else if(recievedPacket.getPort()==22){ //The data must have come from the client, so it is going to the server
                        recievedPacket.setPort(69); //Set the packets port to the server port
                    }
                    queue.add(recievedPacket); //Enqueue the packet
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    /**
     * This method prints the information in recievedPacket, formatted according to if it was sent or recieved
     *
     * @param receivedPacket takes in the packet to be printed
     * @param sending        Boolean value that indicates if the packet is to be sent, or was recieved
     */
    public void printPacket(DatagramPacket receivedPacket, boolean sending) {
        if (!sending) { //If the packet was recieved
            System.out.println(Thread.currentThread().getName() + ": Received the following packet (String): " + new String(receivedPacket.getData())); //Print data as string (Binary values will not appear correctly in the string,
            System.out.println("Recived the following packet (Bytes): "); //but this is what the assignment said to do)
            for (int z = 0; z < receivedPacket.getData().length - 1; z++) { //Prints the byte array one index at a time
                System.out.print(receivedPacket.getData()[z] + ", ");
            }
            System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
            System.out.println("From:" + receivedPacket.getAddress() + " on port: " + receivedPacket.getPort()); //Prints the address and port the packet was recieved on
            System.out.println(""); //Adds a newline between packet sending and receiving
        } else { //The packet is being sent
            System.out.println(Thread.currentThread().getName() + ": Sending the following packet (String): " + new String(receivedPacket.getData()));//Print data as string (Binary values will not appear correctly in the string,
            System.out.println("Sending the following packet (Bytes): "); //but this is what the assignment said to do)
            for (int z = 0; z < receivedPacket.getData().length - 1; z++) { //Prints the byte array one index at a time
                System.out.print(receivedPacket.getData()[z] + ", ");
            }
            System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
            System.out.println("To:" + receivedPacket.getAddress() + " on port: " + receivedPacket.getPort()); //Prints the address and port the packet is being sent to
            System.out.println(""); //Adds a newline between packet sending and receiving

        }
    }

}
