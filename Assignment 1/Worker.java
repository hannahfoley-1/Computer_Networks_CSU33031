//Hannah Foley 20332137

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.SQLOutput;

public class Worker extends Node {

    InetSocketAddress dstAddress;
    boolean available;

    Worker(int port){
        try {
            available = true;
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

            int packetType = content.getType();
            //int packetTopic = content.getTopic();
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.print("Received Ack packet");
                    break;
                case PacketContent.GETFILEINFO:
                    System.out.println("Sending File");
                    //todo: send file back to ingress
                    sendFile(packet);
                    //added below just for the minute to get all 3 pinging - delete later
                    break;
                case PacketContent.RECFILEINFO:
                    System.out.println("Sending File");
                    //todo: send file back to ingress
                    sendFile(packet);
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

        //TODO: GET FILE SIZE
        String filename = "file1.txt";
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
        FileInfoContent fileContent = new FileInfoContent(filename, size);

        System.out.println("Sending packet w/ name: " + filename + " & length: " + size); // Send packet with file name and length
        System.out.println("Client port " + CLIENT_PORT);
        System.out.println("Ingress port " + INGRESS_PORT);
        System.out.println("Worker port " + WORKER_PORT);
        packet= fileContent.toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Packet sent");
        this.wait();
        fin.close();

//
//
//        String fname;
//        File file= null;
//        FileInputStream fin= null;
//
//        FileInfoContent fcontent;
//
//        int size;
//        byte[] buffer= null;
//        buffer = packet.getData();
//        ByteArrayInputStream bstream = new ByteArrayInputStream(buffer);
//        ObjectInputStream ostream = new ObjectInputStream(bstream);
//
//        fname= ostream.readUTF();//terminal.readString("Name of file: ");
//
//        file= new File(fname);				// Reserve buffer for length of file and read file
//        buffer= new byte[(int) file.length()];
//        fin= new FileInputStream(file);
//        size= fin.read(buffer);
//        if (size==-1) {
//            fin.close();
//            throw new Exception("Problem with File Access:"+fname);
//        }
//        System.out.println("File size: " + buffer.length);
//
//        fcontent= new FileInfoContent(fname, size);
//
//        System.out.println("Sending packet w/ name & length"); // Send packet with file name and length
//        System.out.println("Checking changes up to date");
//        packet= fcontent.toDatagramPacket();
//        packet.setSocketAddress(ingressAddress);
//        socket.send(packet);
//        System.out.println("Packet sent");
//        this.wait();
//        fin.close();
    }

    public static void main(String[] args) {
        try {
            (new Worker(WORKER_PORT)).start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
