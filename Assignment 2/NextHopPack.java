import java.io.*;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class NextHopPack extends PacketContent{
    private static InetSocketAddress nextHop;
    private static String nextHopString;
    //static String nextHop;
    //InetSocketAddress nextHop;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    NextHopPack(String nexthop, String destination) {
        type= NEXTHOPPACK;
        nextHopString = nexthop;
        nextHop = Controller.stringToAddress(nexthop);
        this.destination = destination;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected NextHopPack(ObjectInputStream oin, String nextHopString) {
        try {
            type= NEXTHOPPACK;
            //this.app = app;
            this.nextHopString = nextHopString;
            //info= oin.readUTF();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(nextHop.toString());
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public String toString() {
        return "NEXT HOP : " + nextHopString;
    }

    /**
     * Returns the type of the packet.
     *
     * @return Returns the type of the packet.
     */
    public int getType() {
        return type;
    }

    public String getNextHop() {
        return nextHop.toString();
    }

    //public InetSocketAddress getDestination() {
        //return destination;
    //}

    public static InetSocketAddress getNextHopAddress(String nextHopString)
    {
        return Controller.stringToAddress(nextHopString);
    }

    public static InetSocketAddress getNextHopInet(){
        return getNextHopAddress(nextHopString);}


    @Override
    public DatagramPacket toDatagramPacket() {
        DatagramPacket packet = null;

        try {
            ByteArrayOutputStream bout;
            ObjectOutputStream oout;
            byte[] data;

            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);

            oout.writeInt(type);         // write type to stream
            oout.writeUTF(nextHopString);
            oout.writeChar(app);
            toObjectOutputStream(oout);  // write content to stream depending on type
            oout.flush();

            //make byte arrays
            //System.out.println("Making header with app " + app);
            byte[] header = makeHeader(app);
            data = bout.toByteArray(); // convert content to byte array

            //concat these byte arrays header + data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header);
            outputStream.write(data);

            byte[] packetByteArray = outputStream.toByteArray();

            //System.out.println("PACKET BYTE ARRAY " + packetByteArray);
            packet = new DatagramPacket(packetByteArray, OFFSET, data.length);
            oout.close();
            bout.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

}
