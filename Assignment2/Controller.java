import javax.crypto.spec.PSource;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Controller extends Node{

    String[][] completeRoutingTable =
            {
                    {"A", "appAddress"},
                    {"B", "app2Address"}
            };

    Controller(int port){
        try {
            socket = new DatagramSocket(port);
            listener.go();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");

        //pretty print pre defined routing table
        prettyPrintRoutingTable();

        this.wait();
    }

    public static void main(String[] args) {
        try{
            (new Controller(CONTROLLER_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onReceipt(DatagramPacket packet) throws Exception {
        try{
            InetSocketAddress responseAddress = (InetSocketAddress) packet.getSocketAddress();
            //System.out.println("Received a packet from" + responseAddress);
            DatagramPacket response;
            response = new AckPackContent("OK - controller received this").toDatagramPacket();
            response.setSocketAddress(responseAddress);
            socket.send(response);

            PacketContent content= PacketContent.fromDatagramPacket(packet);
            int packetType = content.getType();
            switch(packetType) {
                case ACKPACKET:
                    System.out.println("Received Ack packet");
                    break;
                case HELLOPACK:
                    System.out.println(content.toString());
                    break;
                case REQUESTHOPPACK:
                    //TODO make routing table
                    char destination = content.getApp();
                    System.out.println("Received request to get next hop to ");
                    System.out.println(destination);
                    sendNextHop(responseAddress, destination);
                    break;
                case REQUESTPACK:
                    System.out.println("Error: Controller received request for app");
                    break;
                case APPPACK:
                    System.out.println("Error: Controller received app");
                    break;
                default:
                    System.out.println("Packet type not recognised");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendNextHop(InetSocketAddress responseAddress, char destination) throws IOException {
        String nextHop = null;
        System.out.println("Consulting routing table");
        for(int i = 0; i < completeRoutingTable.length; i++)
        {
            char routingTableDestination = (completeRoutingTable[i][0]).charAt(0);
            //System.out.println("CHAR AT " + i + "0" + " = " + routingTableDestination);
            //System.out.println("Destination : " + destination);
            //if(((Character)routingTableDestination).equals('A') &&
            if(((Character)destination).equals('A'))
            //if(routingTableDestination == destination)
            {
                System.out.println("Next hop found at " + i);
                nextHop = (completeRoutingTable[i][1]);
                System.out.println("NEXT HOP = " + nextHop);
                break;
            }
            else {
                nextHop = (completeRoutingTable[i][1]);
            }
        }

        //TODO: GET NEXT HOP
        DatagramPacket packet = new NextHopPack(nextHop, destination).toDatagramPacket();
        //PacketContent content= PacketContent.fromDatagramPacket(packet);
        packet.setSocketAddress(responseAddress);
        socket.send(packet);
        System.out.println("Sent next hop onto router");
    }

    public static InetSocketAddress stringToAddress(String addressName){
        if(addressName.equalsIgnoreCase("appAddress"))
        {
            return appAddress;
        }
        else return app2Address;
    }

    public void prettyPrintRoutingTable()
    {
        System.out.println("Destination  |  Route Through");
        System.out.println("==============================");
        for (int i = 0; i < completeRoutingTable.length; i++)
        {
            for(int j = 0; j < completeRoutingTable[i].length; j++)
            {
                System.out.print(completeRoutingTable[i][j]);
                if (j != (completeRoutingTable[i].length - 1)) {
                    System.out.print("            | ");
                } else {
                    System.out.print("");
                }
            }
            System.out.println('\n');
        }
    }



}
