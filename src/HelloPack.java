import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

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
//        try {
//            oout.writeUTF(info);
//        }
//        catch(Exception e) {e.printStackTrace();}
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

    @Override
    public DatagramPacket toDatagramPacket() {
        DatagramPacket packet = null;
        //System.out.println("Entering the correct toDatagramPacket for HELLO");

        try {
            ByteArrayOutputStream bout;
            ObjectOutputStream oout;
            byte[] data;

            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);

            oout.writeInt(type);         // write type to stream
            oout.writeUTF(info);
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

    public String getNode(){return null;}

    @Override
    public String[][] getTable() {
        return null;
    }
}
