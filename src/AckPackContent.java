import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AckPackContent extends PacketContent{

    String info;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    AckPackContent(String info) {
        type= ACKPACKET;
        this.info = info;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected AckPackContent(ObjectInputStream oin) {
        try {
            type= ACKPACKET;
            info= oin.readUTF();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(info);
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public String toString() {
        return "ACK:" + info;
    }

    /**
     * Returns the type of the packet.
     *
     * @return Returns the type of the packet.
     */
    public int getType() {
        return type;
    }
    public String getNode(){return null;}
    @Override
    public String[][] getTable() {
        return null;
    }
}
