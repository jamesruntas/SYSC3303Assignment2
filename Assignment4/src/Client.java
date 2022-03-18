package src;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.time.Instant;

public class Client {


    public static void main(String[] args) {
        long startTime = 0;
        long endTime = 0;
   
        try {
            int numberOfSuccessfulPackets = 0;
            DatagramSocket socket = new DatagramSocket(22); //Creates a new socket. This will be used for sending and recieving packets
            //			socket.setSoTimeout(5000); //Sets the timeout value to 5 seconds. If 5 seconds elapses and no packet arrives on receive, an exception will be thrown
            InetAddress local = InetAddress.getLocalHost(); //Gets the local address of the computer

            for (int i = 1; i <= 1000; i++) { //Loops 11 times (starts at 1 -> 11)

                byte[] dataArray = generateByteArray(i);

                DatagramPacket packetToSend = new DatagramPacket(dataArray, dataArray.length, local, 23); //Creates a packet from the dataArray, to be sent to the intermediate host
                DatagramPacket replyPacket = new DatagramPacket(new byte[2000], 2000); //Creates a packet to receive the acknowledgement in.

                socket.send(packetToSend); //Sends the packetToSend
                socket.receive(replyPacket); //Receive the packet from the intermediate host
           
                if(i==1) {
                   startTime = System.nanoTime();
                }

                boolean receieved = false; //defines a flag to check for receiving a actual packet vs a nothing to report packet ("null")
                DatagramPacket receivedPacket = new DatagramPacket(new byte[4], 4); //Creates a new packet for receiving
                byte[] requestByteArray = "request".getBytes(); //Convert "request" into a byte array to send
                int endDetection = 0; //Defines counter which detects the end of the program
                while (!receieved) { //Loop until a not null packet is received
                    DatagramPacket requestPacket = new DatagramPacket(requestByteArray, requestByteArray.length, local, 24);
                    socket.send(requestPacket); //Send a request to the intermediate server
                    //					printPacket(requestPacket, true);
                    socket.receive(receivedPacket); //Receive the response
                   
                    if(i==1000) {                           
                            endTime = System.nanoTime();
                        }

                    if (!(new String(receivedPacket.getData()).trim().equals("NA"))) {//If the response is not null, ie. a actual response
                        numberOfSuccessfulPackets++;
                        System.out.println("Client has received " + numberOfSuccessfulPackets + " packets so far");
                        receieved = true; //Break out of loop
                    }
                    endDetection++;
                    if (endDetection == 5) {
                        break;
                    }
                }
            }
       
            long timeElapsed = endTime - startTime;

            //System.out.println("Execution time in milliseconds: " + timeElapsedNow);
            System.out.println("Execution time in nanoseconds : " +
                    timeElapsed);
            System.out.println("Client is finished. It has receievd " + numberOfSuccessfulPackets + " successful packets");
            socket.close(); //Close the socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static byte[] generateByteArray(int i) {
        Random r = new Random();
        byte[] dataArray = new byte[2000]; //Creates the byte array that contains the data to send.
        if (i == 1000) { //If i=11, create a bad packet with the first two indexes containing 0,0
            dataArray[0] = 0;
            dataArray[1] = 0;
        } else {
            if (i % 2 == 0) { //This will alternate between read and write requests as i increments
                dataArray[0] = 0;
                dataArray[1] = 1;
            } else {
                dataArray[0] = 0;
                dataArray[1] = 2;
            }
        }

        String first = "";
        for(int j = 0;j < 996; j++){
            first += "a";
        }
        String fileName = first + ".txt";

        byte[] fileNameByteArray = fileName.getBytes(); //Converts test.txt to an array of bytes
        int x = 0;
        while (x < fileNameByteArray.length) { //puts the new array of bytes into the working dataArray. (x is saved for future use)
            dataArray[2 + x] = fileNameByteArray[x]; //Starts at index 2, and iterates through the fileName byte array
            x++;
        }
        dataArray[3 + x] = 0; //Adds a 0 in between the two strings
        String mode = "octet"; //Defines the mode as octet
        byte[] modeByteArray = mode.getBytes(); //Converts to byte array
        int y = 0;
        while (y < modeByteArray.length) {
            dataArray[3 + x + y] = modeByteArray[y]; //Starting at the 3+xth index, copy the mode byte array into the dataArray
            y++;
        }
        dataArray[x + y + 3] = 0;//Ends the byte array with a 0
        return dataArray;
    }

}
