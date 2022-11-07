import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HelloPack extends PacketContent{
    String info;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    HelloPack(String info) {
        type= HELLOPACK;
        this.info = info;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected HelloPack(ObjectInputStream oin) {
        try {
            type= HELLOPACK;
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
        return "HELLO: " + info;
    }

//    /**
//     * Returns the type of the packet.
//     *
//     * @return Returns the type of the packet.
//     */
//    public int getType() {
//        return type;
//    }
}
