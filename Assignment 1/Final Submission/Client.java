//Hannah Foley 20332137

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Client extends Node {

    //client always sends to ingress
    static final String DEFAULT_DST_NODE = "ingress";

    InetSocketAddress dstAddress;
    static boolean fileReceived = false;

    /**
     * Constructor
     *
     * Attempts to create socket at given port and create an InetSocketAddress for the destinations
     */
    Client(InetSocketAddress dstAddress, int srcPort){
        try {
            //dstAddress= new InetSocketAddress(dstHost, dstPort);
            this.dstAddress = dstAddress;
            socket= new DatagramSocket(srcPort);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }

    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public synchronized void onReceipt(DatagramPacket packet) throws Exception {
        System.out.println("Packet received");

//        DatagramPacket response;
//        //acknowledge receipt
//        response = new AckPacketContent("OK - Received this").toDatagramPacket();
//        response.setSocketAddress(packet.getSocketAddress());
//        socket.send(response);

        PacketContent content= PacketContent.fromDatagramPacket(packet);
        int packetType = content.getType();
        switch(packetType) {
            case PacketContent.ACKPACKET:
                System.out.println("Received Ack packet");
                break;

            case PacketContent.GETFILEINFO:
                System.out.println("Error: Client has received request to get file");
                break;

            case PacketContent.RECFILEINFO:
                System.out.println("Received File");
                fileReceived = true;
                receivedFile(packet);
                break;

            default:
                System.out.println("Packet type not recognised");
                break;
        }
    }

    /**
     * Sender Method
     *
     */
    public synchronized void start() throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter '1' to request file 1 \n" +
                "Enter '2' to request file 2 \n");
        int fileNumber = input.nextInt();

        String fname;
        switch (fileNumber){
            case 1: fname = "file1.txt";
                    break;
            case 2: fname = "file2.txt";
                    break;
                    //todo: SORT SOMETHING NEW FOR DEFAULT??
            default: fname = "file1.txt";
        }
        sendRequest(fname, fileNumber);
    }

    public synchronized void sendRequest(String filename, int fileNumber) throws Exception {
        DatagramPacket packet;

        File file= new File(filename);
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int size= fin.read(buffer);
        if (size==-1) {
            fin.close();
            throw new Exception("Problem with File Access:"+ filename);
        }
        //System.out.println("File size: " + buffer.length);

        FileInfoContent fileRequest = new FileInfoContent(filename, size, fileNumber);

        //FileInfoContent fileRequest = new FileInfoContent(filename, size, fileNumber);
        System.out.println("Requesting packet w/ name: " + filename ); // Send packet with file name and length
//        System.out.println("Client port " + CLIENT_PORT);
//        System.out.println("Ingress port " + INGRESS_PORT);
//        System.out.println("Worker port " + WORKER_PORT);
        packet= fileRequest.toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Packet sent");
        this.wait();
        fin.close();
    }

    public synchronized void receivedFile(DatagramPacket packet)
    {
        PacketContent content= PacketContent.fromDatagramPacket(packet);
        String filename = content.returnFileName();
        if(filename.equalsIgnoreCase(""))
        {
            System.out.println("File too big to be send");
        }
        else
        {
            System.out.println("Client has received the file - Thank you!!");
            System.out.println("File contents are as follows:");

            System.out.println(content.toString());
        }
    }

    /**
     * Test method
     *
     * Sends a packet to a given address
     */
    public static void main(String[] args) {
        try {
            //(new Client(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
            while(!fileReceived) {
                (new Client(ingressAddress, CLIENT_PORT)).start();
                System.out.println("Program completed");
            }
        } catch(java.lang.Exception e) {e.printStackTrace();
            System.out.print(DEFAULT_DST_NODE);}
    }
}


