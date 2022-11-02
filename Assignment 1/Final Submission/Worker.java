//Hannah Foley 20332137

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Worker extends Node {

    InetSocketAddress dstAddress;

    Worker(int port){
        try {
            //worker only sends back to ingress
            dstAddress = ingressAddress;
            socket = new DatagramSocket(port);
            listener.go();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void onReceipt(DatagramPacket packet){
        try {
            System.out.println("Packet was received");

            PacketContent content= PacketContent.fromDatagramPacket(packet);

            //acknowledge receipt
//            DatagramPacket response;
//            response = new AckPacketContent("OK - Worker received this").toDatagramPacket();
//            response.setSocketAddress(packet.getSocketAddress());
//            socket.send(response);

            int packetType = content.getType();
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.print("Received Ack packet");
                    break;
                case PacketContent.GETFILEINFO:
                    System.out.println("Sending File");
                    sendFile(packet);
                    break;
                case PacketContent.RECFILEINFO:
                    System.out.println("Error: Wrong packet reached worker");
                    //a packet containing file info should not reach back to worker
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");
        this.wait();
    }

    public synchronized void sendFile(DatagramPacket packet) throws Exception {
        //DatagramPacket packet;

        PacketContent content = PacketContent.fromDatagramPacket(packet);
        //int [] header = content.returnHeader();
        int fileNumber = content.getFileNumber();
        System.out.println("filenumber:" + fileNumber);
        String filename = "";
        if(fileNumber == 1)
        {
            filename = "file1.txt";
        }
        else
        {
            filename = "file2.txt";
        }
        File file= new File(filename);
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int size= fin.read(buffer);
        //int size = 655365678;
        if (size==-1) {
            fin.close();
            throw new Exception("Problem with File Access:"+ filename);
        }
        System.out.println("File size: " + buffer.length);

        ReceiveFileContent fileContent;

        if(size >= content.MTU){
            System.out.println("File too big ");
            fileContent = new ReceiveFileContent("", size, fileNumber);
        }
        else
        {
            fileContent = new ReceiveFileContent(filename, size, fileNumber);

        }

        System.out.println("Sending packet w/ name: " + filename + " & length: " + size); // Send packet with file name and length
        //System.out.println("Client port " + CLIENT_PORT);
        //System.out.println("Ingress port " + INGRESS_PORT);
        //System.out.println("Worker port " + WORKER_PORT);
        packet= fileContent.toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Packet sent");
        //this.wait();
        fin.close();

    }

    public static void main(String[] args) {
        try {
            (new Worker(WORKER_PORT)).start();
            //(new Worker(WORKER2_PORT)).start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
