//Hannah Foley 20332137

import javax.sound.midi.Soundbank;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.SQLOutput;

public class Ingress extends Node{
    //final int WORKER1 = 500001;
    //final int WORKER2 = 500002;

    Ingress(int port){
        try {
            socket = new DatagramSocket(port);
            listener.go();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void onReceipt(DatagramPacket packet){
        try {
            System.out.println("Received a packet");
            DatagramPacket response;

            //TODO: if packet is coming from a client - is the client authorized

            //acknowledge receipt
            response = new AckPacketContent("OK - Received this").toDatagramPacket();
            response.setSocketAddress(packet.getSocketAddress());
            socket.send(response);

            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            System.out.println(packetType);
            //int packetTopic = content.getTopic();
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.println("Received Ack packet");
                    this.wait();
                    break;

                case PacketContent.GETFILEINFO:
                    System.out.println("Received request to get file");
                    getFile(packet);
                    //TODO: Send onto requested worker asking for file

                case PacketContent.RECFILEINFO:
                    System.out.println("Received File");
                    sendFile(packet);
                    //TODO: Send back onto the client
            }
    }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");
        this.wait();
    }

    public synchronized void getFile(DatagramPacket packet) throws IOException, InterruptedException {
        DatagramPacket packetCopy = packet;
        //TODO: Set address to send to
        packetCopy.setSocketAddress(workerAddress);
        socket.send(packetCopy);
        System.out.println("Ingress sent file onto client ");
        //this.wait();
    }

    public synchronized void sendFile(DatagramPacket packet) throws IOException, InterruptedException {
        DatagramPacket packetCopy = packet;
        //TODO: set address to send to
        packetCopy.setSocketAddress(workerAddress);
        //changed this for the minute just to get all 3 pinging
        //packetCopy.setSocketAddress(ingressAddress);
        socket.send(packetCopy);
        System.out.println("Ingress sent request onto worker ");
        //this.wait();
    }


    public static void main(String[] args) {
        try{
            (new Ingress(INGRESS_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
