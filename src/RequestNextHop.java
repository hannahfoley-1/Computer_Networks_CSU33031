import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

public class RequestNextHop extends PacketContent{
    String nextHop;


    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    RequestNextHop(String destination, InetSocketAddress currentNode) {
        type= REQUESTHOPPACK;
        app = destination.charAt(0);
        this.destination = destination;
        //nextHop = "appAddress";
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected RequestNextHop(ObjectInputStream oin, char app, String destination) {
        try {
            type= REQUESTHOPPACK;
            this.app = app;
            this.destination = destination;
            //System.out.println("APP char read in " + app);
            //System.out.println("Destination = " + destination);
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
            System.out.println(toString());
            oout.writeUTF(String.valueOf(app));
            oout.writeUTF(destination);
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public String toString() {
        return "NEXT HOP REQUESTED FOR DESTINATION: " + destination;
    }

    /**
     * Returns the type of the packet.
     *
     * @return Returns the type of the packet.
     */
    public int getType() {
        return type;
    }

    @Override
    public String getNode() {
        return null;
    }
    @Override
    public String[][] getTable() {
        return null;
    }

    //public char getDestination() { return app; }
}
