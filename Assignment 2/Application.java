import java.io.IOException;
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

            InetSocketAddress responseAddress = (InetSocketAddress) packet.getSocketAddress();
            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            switch (packetType) {
                case PacketContent.ACKPACKET:
                    System.out.println("Received Ack packet");
                    break;
                case PacketContent.REQUESTPACK:
                    System.out.println("Received Request");
                    System.out.println(content.toString());
                    sendApp(responseAddress);
                    break;
                default:
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

    public void sendApp(InetSocketAddress responseAddress) throws IOException {
        //TODO: FIX
        DatagramPacket packet = new AppPackContent('A', employeeAddress.getHostName()).toDatagramPacket();
        packet.setSocketAddress(router4Address);
        socket.send(packet);
        System.out.println("Sent app onto router 4 ");
    }
}
