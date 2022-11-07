import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Application extends Node{

    Application(int port){
        try {
            socket = new DatagramSocket(port);
            listener.go();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceipt(DatagramPacket packet) throws Exception {
        try {
            System.out.println("Packet was received");

            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            switch (packetType) {
                case PacketContent.ACKPACKET:
                    System.out.println("Received Ack packet");
                    break;
                case PacketContent.REQUESTPACK:
                    System.out.println("Received Request");
                    System.out.println(content.toString());
                    //TODO: SEND APP
                    break;
                case PacketContent.APPPACK:
                    System.out.println("Error: Wrong packet reached Application");
                    break;
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");
        this.wait();
    }


    public static void main(String[] args) {
        try {
            (new Application(APP_PORT)).start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
