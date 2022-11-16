import javax.crypto.spec.PSource;
import javax.sound.midi.Soundbank;
import javax.sql.rowset.JdbcRowSet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Controller extends Node{

    String[][] completeRoutingTable =
            {
                    {"employee", "employee"},
                    {"router", "routerAddress"},
                    {"router2", "router2Address"},
                    {"router3", "router3Address"},
                    {"router4", "router4Address"},
                    {"A", "appAddress"},
                    {"B", "app2Address"}
            };

    String [] routeToAppA = {"router", "router2", "router4", "A"};
    String [] routeToAppB = {"router", "router2", "router4", "B"};
    String [] routeToEmployee = {"router4", "router2", "router", "employee"};

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
                    String destination = content.getDestinationString();
                    System.out.print("Received request to get next hop to ");
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

    public void sendNextHop(InetSocketAddress responseAddress, String destination) throws IOException {
        String nextHop = null;
        System.out.println("Consulting routing table for destination " + destination);
        String [] routeToTake = null;

        if(destination.equals("A"))
        {
            routeToTake = routeToAppA;
        }
        else if (destination.equals("B"))
        {
            routeToTake = routeToAppB;
        }
        else if (destination.equalsIgnoreCase("employee") || destination.equalsIgnoreCase("e"))
        {
            routeToTake = routeToEmployee;
        }

        String responseHostName = responseAddress.getHostName();
        int dot = responseHostName.indexOf(".");
        if(dot != -1)
        {
            responseHostName = responseHostName.substring(0, dot);
        }

        for(int i = 0; i < routeToTake.length && nextHop == null; i++)
        {
            if(routeToTake[i].equalsIgnoreCase(responseHostName))
            {
                nextHop = routeToTake[i+1];

            }
        }


//        for(int i = 0; i < completeRoutingTable.length && nextHop == null; i++)
//        {
//            char routingTableDestination = (completeRoutingTable[i][0]).charAt(0);
//            //System.out.println("CHAR AT " + i + "0" + " = " + routingTableDestination);
//            //System.out.println("Destination : " + destination);
//            //if(((Character)routingTableDestination).equals('A') &&
//            if(destination.equals("A"))
//            //if(routingTableDestination == destination)
//            {
//                //System.out.println("Next hop found at " + i);
//                nextHop = (completeRoutingTable[i][1]);
//                break;
//            }
//            else if (destination.equals("B")) {
//                //System.out.println("Next hop found at " + i);
//                nextHop = (completeRoutingTable[i][1]);
//                break;
//            }
//            else if (i == completeRoutingTable.length - 1)
//            {
//                //System.out.println("Next hop found at " + i);
//                nextHop = (completeRoutingTable[i][1]);
//                break;
//            }
//        }

        System.out.println("NEXT HOP = " + nextHop);
        DatagramPacket packet = new NextHopPack(nextHop, destination).toDatagramPacket();
        //PacketContent content= PacketContent.fromDatagramPacket(packet);
        packet.setSocketAddress(responseAddress);
        socket.send(packet);
        System.out.println("Sent next hop onto router");
    }

    public static InetSocketAddress stringToAddress(String addressName){
        switch(addressName){
            case "appAddress":
            case "A":
                return appAddress;
            case "app2Address":
            case "B":
                return app2Address;
            case "employee":
                return employeeAddress;
            case "router":
                return routerAddress;
            case "router2":
                return router2Address;
            case "router3":
                return router3Address;
            case "router4":
                return router4Address;
            default:
                return app2Address;
        }
    }

    public void prettyPrintRoutingTable()
    {
        System.out.println("Destination  |  Route Through");
        System.out.println("==============================");
        int halfway = 14;
        for (int i = 0; i < completeRoutingTable.length; i++)
        {
            for(int j = 0; j < completeRoutingTable[i].length; j++)
            {
                System.out.print(completeRoutingTable[i][j]);
                int charsIn = completeRoutingTable[i][j].length();
                int spaces = halfway - charsIn;
                for(int k = 0; k < spaces; k ++)
                {
                    System.out.print(" ");
                }
                if (j < (completeRoutingTable[i].length - 1)) {
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
