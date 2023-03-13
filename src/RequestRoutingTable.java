import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

public class RequestRoutingTable extends PacketContent {
    String node;

    RequestRoutingTable(String node)
    {
        type = REQUESTROUTINGTABLE;
        //System.out.println("Node before allocation " + node);
        this.node = node;
        //System.out.println("Node in constructor " + this.node);
    }

    @Override
    protected void toObjectOutputStream(ObjectOutputStream oout) throws IOException {
        oout.writeUTF(node);
    }

    protected RequestRoutingTable(ObjectInputStream oin, String node) {
        try {
            type= REQUESTROUTINGTABLE;
            this.node = node;
            //this.app = app;
            //this.destination = destination;
            //System.out.println("APP char read in " + app);
            //System.out.println("Destination = " + destination);
            //info= oin.readUTF();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    public String getNode(){return new String(node);}

    public String toString() {
        return "ROUTING TABLE REQUESTED FOR NODE: " + node;
    }

    public int getType() {
        return type;
    }

    @Override
    public String[][] getTable() {
        return null;
    }

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
            //System.out.println("NODE " + node);
            oout.writeUTF(node);
            //oout.writeUTF(destination);
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
