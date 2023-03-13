import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Router extends Node{
    char app;
    DatagramPacket lastReceivedPacket;

    String[][] relevantRoutingTable;

    Router(int port){
        try {
            socket = new DatagramSocket(port);
            listener.go();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onReceipt(DatagramPacket packet) throws Exception {
        try{
            System.out.println("Received a packet");
//            DatagramPacket response;
//            InetSocketAddress responseAddressDestination = (InetSocketAddress) packet.getSocketAddress();
//            response = new AckPackContent("OK - router received this").toDatagramPacket();
//            response.setSocketAddress(responseAddressDestination);
//            socket.send(response);

            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.println("Received Ack packet");
                    break;
                case PacketContent.REQUESTPACK:
                    lastReceivedPacket = packet;
                    System.out.println("Received request for app");
                    app = content.getApp();
                    System.out.println("App requested = " + app);
                    consultMasterRoutingTable(String.valueOf(app));
                    break;
                case PacketContent.APPPACK:
                    lastReceivedPacket = packet;
                    System.out.println("Received app");
                    System.out.println("Destination to send app: " + content.getDestinationString());
                    consultMasterRoutingTable(content.getDestinationString());
                    break;
                case NEXTHOPPACK:
                    System.out.println(content.toString());
                    InetSocketAddress nextHop = NextHopPack.getNextHopInet();
                    forwardPacket(lastReceivedPacket, nextHop);
                    break;
                case ROUTINGTABLE:
                    extractRoutingTableFromPacket(content);
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
        //this.relevantRoutingTable = getRelevantRoutingTable();
        getRelevantRoutingTable();
        System.out.println("Waiting for contact");
        this.wait();
    }

    public static void main(String[] args) {
        try{
            (new Router(ROUTER_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void sendHello() throws IOException {
        //byte[] data = new byte[1];
        //data[TYPEPOS] = HELLOPACK;
        InetSocketAddress dstAddress = controllerAddress;
        DatagramPacket packet = new HelloPack("Hello sent from router").toDatagramPacket();
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

    public void getRelevantRoutingTable() throws IOException {
        InetSocketAddress dstAddress = controllerAddress;
        //String[][] relevantTable = Controller.whatIsRelevantForNode("router");
        //System.out.println("I AM GOING INTO ROUTER 1 METHOD ;/");
        //RequestRoutingTable rrt = new RequestRoutingTable("router");
        //System.out.println("NODE BEFORE GOING IN " + rrt.node);
        DatagramPacket routingTableRequest = new RequestRoutingTable("router").toDatagramPacket();
        forwardPacket(routingTableRequest, dstAddress);

    }

    public void forwardPacket(DatagramPacket packet, InetSocketAddress destAddress) throws IOException {
        DatagramPacket packetCopy = packet;
        packet.setSocketAddress(destAddress);
        socket.send(packetCopy);
    }

    void extractRoutingTableFromPacket(PacketContent packet)
    {
        System.out.println("Received specific routing table for this node");
        String [][] arr = packet.getTable();
        prettyPrintRoutingTable(arr);
//        byte[] table = packet.getData();
//        System.out.println(String.valueOf(table));
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

    public void prettyPrintRoutingTable(String [][] arr)
    {
        System.out.println("     From     | Route Through |  Destination  ");
        System.out.println("==============================================");
        int oneThird = 15;
        for (int i = 0; i < arr.length; i++)
        {
            for(int j = 0; j < arr[i].length; j++)
            {
                System.out.print(arr[i][j]);
                int charsIn = arr[i][j].length();
                int spaces = oneThird - charsIn;
                for(int k = 0; k < spaces; k ++)
                {
                    System.out.print(" ");
                }
                if (j < (arr[i].length - 1)) {
                    //&& i != completeRoutingTable.length-1t
                    System.out.print("| ");
                }
                else {
                    System.out.print("");
                }
            }
            System.out.println('\n');
        }


    }




}
