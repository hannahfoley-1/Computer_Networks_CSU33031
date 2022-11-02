//Hannah Foley 20332137

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Ingress extends Node{
    public static final int WORKER2_PORT = 50093;
    public static final InetSocketAddress worker2Address = new InetSocketAddress("part2worker21", WORKER2_PORT);


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

            //TODO: if packet is coming from a client - is the client authorised

            //acknowledge receipt
            response = new AckPacketContent("OK - Ingress received this").toDatagramPacket();
            response.setSocketAddress(packet.getSocketAddress());
            socket.send(response);

            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.println("Received Ack packet");
                    break;

                case PacketContent.GETFILEINFO:
                    System.out.println("Received request to get file");
                    getFile(packet);
                    break;

                case PacketContent.RECFILEINFO:
                    System.out.println("Received File");
                    sendFile(packet);
                    break;

                default:
                    System.out.println("Packet type not recognised");
                    break;
            }
    }catch (Exception e){
            System.out.println("Packet type not recognised or file too big to send onto client");
            e.printStackTrace();
        }
    }

    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");
        this.wait();
    }

    public synchronized void getFile(DatagramPacket packet) throws IOException, InterruptedException {
        PacketContent content = PacketContent.fromDatagramPacket(packet);
        int fileno = content.getFilenumber();
        InetSocketAddress workerToSendTo = workerAddress;
        if(fileno == 1){
            workerToSendTo = workerAddress;
            System.out.println("SENDING TO WORKER 1");
        }
        else if (fileno == 2){
            workerToSendTo = worker2Address;
            System.out.println("SENDING TO WORKER 2");
        }
        else{
            System.out.println("ERROR: DONT KNOW WHICH WORKER TO SEND TO");
        }

        System.out.println("Sending to worker " + workerToSendTo.getHostName());
        //System.out.println("port: " + workerToSendTo.getPort());
        //System.out.println("WORKER ADDRESS: " + workerToSendTo.getAddress());
        //System.out.println("INGRESS ADDRESS: " + ingressAddress.getAddress());
        //System.out.println("CLIENT ADDRESS: " + clientAddress.getAddress());
        //System.out.println("RESOLVED?: " + workerToSendTo.isUnresolved());

        DatagramPacket packetCopy = packet;
        packetCopy.setSocketAddress(workerToSendTo);
        socket.send(packetCopy);
        System.out.println("Ingress has sent request for file onto worker ");
    }

    public synchronized void sendFile(DatagramPacket packet) throws IOException, InterruptedException {
        DatagramPacket packetCopy = packet;

        packetCopy.setSocketAddress(clientAddress);
        socket.send(packetCopy);
        System.out.println("Ingress has sent file onto client ");
    }


    public static void main(String[] args) {
        try{
            (new Ingress(INGRESS_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
