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
    //static final int DEFAULT_SRC_PORT = 50000;
    //static final int DEFAULT_DST_PORT = 50001;
    static final String DEFAULT_DST_NODE = "ingress";

    InetSocketAddress dstAddress;

    /**
     * Constructor
     *
     * Attempts to create socket at given port and create an InetSocketAddress for the destinations
     */
    //Client(String dstHost, int dstPort, int srcPort) {
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
    public synchronized void onReceipt(DatagramPacket packet) {
        PacketContent content= PacketContent.fromDatagramPacket(packet);
        System.out.println(content.toString());
        this.notify();
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
            default: fname = "file1.txt";
        }
        sendRequest(fname);



        //TODO: send request to ingress asking for file
    }

    public synchronized void sendRequest(String filename) throws Exception {
        DatagramPacket packet;

        //TODO: GET FILE SIZE
        File file= new File(filename);
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int size= fin.read(buffer);
        if (size==-1) {
            fin.close();
            throw new Exception("Problem with File Access:"+ filename);
        }
        System.out.println("File size: " + buffer.length);

        //TODO: SEND PACKET TO INGRESS
        ReceiveFileContent fileRequest = new ReceiveFileContent(filename, size);

        System.out.println("Requesting packet w/ name: " + filename + " & length: " + size); // Send packet with file name and length
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

    /**
     * Test method
     *
     * Sends a packet to a given address
     */
    public static void main(String[] args) {
        try {
            //(new Client(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
            (new Client(ingressAddress, CLIENT_PORT)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();
            System.out.print(DEFAULT_DST_NODE);}
    }
}


