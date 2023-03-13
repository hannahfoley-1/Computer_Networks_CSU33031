import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Router2 extends Node{
    char app;
    DatagramPacket lastReceivedPacket;

    Router2(int port){
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
                    //String nextHopString = NextHopPack.getNextHop();
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
                case ROUTINGTABLE:
                    extractRoutingTableFromPacket(content);
                    break;
                default:
                    System.out.println("Wrong packet type reached the Router");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public synchronized void start() throws Exception {
        //ESTABLISH CONNECTION WITH CONTROLLER
        sendHello();
        getRelevantRoutingTable();
        System.out.println("Waiting for contact");
        this.wait();
    }


    public static void main(String[] args) {
        try{
            (new Router2(ROUTER2_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void sendHello() throws IOException {
        InetSocketAddress dstAddress = controllerAddress;
        DatagramPacket packet = new HelloPack("Hello sent from router2").toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
    }


    public void getRelevantRoutingTable() throws IOException {
        InetSocketAddress dstAddress = controllerAddress;
        //String[][] relevantTable = Controller.whatIsRelevantForNode("router");
        RequestRoutingTable rrt = new RequestRoutingTable("router2");
        //System.out.println("NODE BEFORE GOING IN " + rrt.node);
        DatagramPacket routingTableRequest = new RequestRoutingTable("router2").toDatagramPacket();
        forwardPacket(routingTableRequest, dstAddress);

    }


    public synchronized void consultMasterRoutingTable(String destination) throws IOException {
        InetSocketAddress dstAddress = controllerAddress;
        System.out.println("DESTINATION: " + destination);
        DatagramPacket packet = new RequestNextHop(destination, routerAddress).toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Sent request for next hop");
    }

    //TODO: Consult local routing table

    void extractRoutingTableFromPacket(PacketContent packet)
    {
        System.out.println("Received specific routing table for this node");
        String [][] arr = packet.getTable();
        prettyPrintRoutingTable(arr);
//        byte[] table = packet.getData();
//        System.out.println(String.valueOf(table));
    }

    public void forwardPacket(DatagramPacket packet, InetSocketAddress destAddress) throws IOException {
        DatagramPacket packetCopy = packet;
        packet.setSocketAddress(destAddress);
        socket.send(packetCopy);
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
