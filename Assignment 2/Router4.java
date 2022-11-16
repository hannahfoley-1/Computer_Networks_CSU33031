import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Router4 extends Node{
    char app;
    DatagramPacket lastReceivedApp;
    DatagramPacket lastReceivedRequest;
    DatagramPacket lastReceivedPacket;


    Router4(int port){
        try {
            socket = new DatagramSocket(port);
            listener.go();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onReceipt(DatagramPacket packet) throws Exception {
        try {
            System.out.println("Received a packet");
            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            //System.out.println("Type " + packetType);
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.println("Received Ack packet");
                    break;
                case PacketContent.REQUESTPACK:
                    lastReceivedPacket = packet;
                    System.out.println("Received request for app");
                    //TODO: Extract app from request
                    app = content.getApp();
                    System.out.println("App requested = " + app);
                    consultMasterRoutingTable(String.valueOf(app));
                    //TODO: Extract app from request
                    //TODO: getFile(packet);
                    break;
                case PacketContent.APPPACK:
                    lastReceivedPacket = packet;
                    System.out.println("Received app");
                    //app = content.getApp();
                    //TODO FIX THIS: - get it to use the master routing table
                    System.out.println("Destination to send app: " + content.getDestinationString());
                    consultMasterRoutingTable(content.getDestinationString());
                    //forwardAppPacket(Controller.stringToAddress(content.getDestinationString()));
                    //TODO: sendFile(packet);
                    break;
                case NEXTHOPPACK:
                    System.out.println(content.toString());
                    //String destination = content.toString();
                    //TODO: GET THIS FROM NEXT HOP
                    //tring nextHopString = NextHopPack.getNextHop();
                    InetSocketAddress nextHop = NextHopPack.getNextHopInet();
                    //= NextHopPack.next;
//                    if(nextHop == employeeAddress)
//                    {
//                        forwardAppPacket(nextHop);
//                    }
//                    else {
//                        forwardReqPacket(nextHop, app);
//                    }
                    forwardPacket(lastReceivedPacket, nextHop);
                    break;
                default:
                    System.out.println("Packet type not recognised");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void start() throws Exception {
        //ESTABLISH CONNECTION WITH CONTROLLER
        sendHello();
        System.out.println("Waiting for contact");
        this.wait();
    }

    public static void main(String[] args) {
        try{
            (new Router(ROUTER4_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void sendHello() throws IOException {
        //byte[] data = new byte[1];
        //data[TYPEPOS] = HELLOPACK;
        InetSocketAddress dstAddress = controllerAddress;
        DatagramPacket packet = new HelloPack("Hello sent from router4").toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
    }

    public synchronized void consultMasterRoutingTable(String destination) throws IOException {
        InetSocketAddress dstAddress = controllerAddress;
        System.out.println("DESTINATION: " + destination);
        DatagramPacket packet = new RequestNextHop(destination, routerAddress).toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Sent request for next hop");
    }

    public void forwardPacket(DatagramPacket packet, InetSocketAddress destAddress) throws IOException {
        DatagramPacket packetCopy = packet;
        packet.setSocketAddress(destAddress);
        socket.send(packetCopy);
    }

    public void forwardReqPacket(InetSocketAddress address, char app) throws IOException {
        DatagramPacket packet = new RequestAppContent(app).toDatagramPacket();

        packet.setSocketAddress(address);
        socket.send(packet);
        System.out.println("Request for App " + app + " forwarded");
    }

    public void forwardAppPacket(InetSocketAddress address) throws IOException {
        DatagramPacket packet = new AppPackContent('A', "employee").toDatagramPacket();
        packet.setSocketAddress(address);
        socket.send(packet);
        System.out.println("App " + app + " forwarded to " + address.getHostName());
    }
}
