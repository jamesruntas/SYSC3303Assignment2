package src;

import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class Server {


    public static void main(String[] args) {
        Thread serverToClientThread = new Thread(new Host(24));
        serverToClientThread.setName("ServerToClient");
        serverToClientThread.start();
        Thread clientToServerThread = new Thread(new Host(23));
        clientToServerThread.setName("ClientToServer");
        clientToServerThread.start();




        int messagesProcessed =0;
        try {
            DatagramSocket socket = new DatagramSocket(69);	//Creates socket bound to port 69

            while(true) {
                byte[] requestByteArray = "request".getBytes();
                boolean receieved = false; //defines a flag to check for receieving a actual packet vs a nothing to report packet ("null")
                DatagramPacket recievedPacket = new DatagramPacket(new byte[2000], 2000);	//Creates a packet to recieve into
                DatagramPacket requestPacket = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 23);

                while(!receieved) {	//Loop until a non null packet is recieved
//					printPacket(requestPacket, true);
                    socket.send(requestPacket);	//Send a request to the intermediate server
                    socket.receive(recievedPacket);	//Receive the response
//					printPacket(recievedPacket, false);
                    if(!(new String(recievedPacket.getData()).trim().equals("NA"))) {//If the response is not null, ie. a actual response
                        receieved=true;	//Break out of loop
                    }

                }

                byte[] dataArray = recievedPacket.getData();	//get the data from the packet to analyze
                if(dataArray[0]==0 && (dataArray[1]== 1 || dataArray[1]== 2)) {	//If the prefix is not invalid
                    int lengthOfFirstWord = checkFirstString(dataArray);	//Get the length of the first string
                    int lengthOfSecondWord = checkSecondString(dataArray, lengthOfFirstWord);	//get the length of the second string
                    if(dataArray[3+lengthOfFirstWord+lengthOfSecondWord] != 0) {	//if the last byte is not a trailing 0
                        throw new IOException("Bad Packet (No trailing 0)");	//Throw exception telling the user that the packet was bad
                    }
                }else {
                    throw new IOException("Bad Packet (Invalid Request)");		//If the prefix was invalid tell the user the request was invalid
                }

                byte[] dataToSend = new byte[4];	//Create byte array to send
                messagesProcessed++;
                if(dataArray[0]==0 && dataArray[1]==1 ) {	//If the request was a read request
                    dataToSend[1]= 3;	//Set the data to 0 3 0 1
                    dataToSend[3] = 1;
                }else {	//The request was a write request
                    dataToSend[1]= 4; 	//Set the data to 0 4 0 0
                    dataToSend[3] =0;
                }

                System.out.println(messagesProcessed + " messages processed so far");
                DatagramPacket packetToSend = new DatagramPacket(dataToSend, dataToSend.length, InetAddress.getLocalHost(), 24);	//Creates a packet to send
//					printPacket(packetToSend, true);	//Prints the packet to be send

                socket.send(packetToSend);	//Sends the packet
                socket.receive(recievedPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    /**
     * 	This method looks at the first string in the packet and determines it's length
     * @param dataArray takes in the packet's dataArray to be analyzed
     * @return the length of the first string
     */
    public static int checkFirstString(byte[] dataArray) {
        boolean atEndOfWord = false;	//set to true if at the end of the word
        int x=0;
        while(!atEndOfWord) {		//While not at the end of the string
            if(dataArray[2+x]!=0) {	//Checks 2+xth index for end of string (2+x because the first 2 indexes are the read/write prefix
                x++;	//If not increment
            }else {
                atEndOfWord=true;	//If so, set end of word flag
            }
        }
        return x;	//Return length of first string
    }
    /**
     * This method looks at the second string in the packet and determines it's length
     * @param dataArray takes in the packet's dataArray to be analyzed
     * @param startOfWordIndex The index where the first word ended
     * @return the length of the first string
     */
    public static int checkSecondString(byte[] dataArray, int startOfWordIndex) {
        boolean atEndOfWord = false;	//set to true if at the end of the word
        int x=0;
        while(!atEndOfWord) {	//While not at the end of the string
            if(dataArray[3+x+startOfWordIndex]!=0) { //Checks 3+x+startOfWordIndex index for end of string
                //(3+x+startOfWordIndex because the first 2 indexes are the read/write prefix + length of first string + 0 between strings
                x++;		//If not, increment
            }else {
                atEndOfWord=true;	//If so, set end of word flag
            }
        }
        return x;	//Return length of second string
    }



}

