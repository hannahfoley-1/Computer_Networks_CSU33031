import java.io.*;
import java.net.DatagramPacket;

public abstract class PacketContent {
    public static final int ACKPACKET = 1;
    public static final int REQUESTPACK = 2;
    public static final int APPPACK = 3;
    public static final int HELLOPACK = 4;
    public static final int REQUESTHOPPACK = 5;
    public static final int NEXTHOPPACK = 6;


    public static final int OFFSET = 1;
    //private static char app;


    int type;
    char app;

    /**
     * Constructs an object out of a datagram packet.
     *
     * @param packet Packet to analyse.
     */
    public static PacketContent fromDatagramPacket(DatagramPacket packet) {
        PacketContent content = null;

        try {
            int type;
            char app;
            byte[] data;
            ByteArrayInputStream bin;
            ObjectInputStream oin;

            data = packet.getData();  // use packet content as seed for stream
            bin = new ByteArrayInputStream(data);
            oin = new ObjectInputStream(bin);

            type = oin.readInt();  // read type from beginning of packet
            //System.out.println("APP FROM DATAGRAM PACKET" + " " + app);
            //TODO : READ IN APP
            //System.out.println("TYPE " + type);
            //app = 'A';

            switch (type) {   // depending on type create content object
                case ACKPACKET:
                    content = new AckPackContent(oin);
                    break;
                case REQUESTPACK:
                    app = oin.readChar();
                    System.out.println("APP FROM DATAGRAM PACKET" + " " + (char)app);
                    content = new RequestAppContent(oin, app);
                    break;
                case APPPACK:
                    app = oin.readChar();
                    System.out.println("APP FROM DATAGRAM PACKET" + " " + (char)app);
                    content = new AppPackContent(oin, app);
                    break;
                case HELLOPACK:
                    content = new HelloPack(oin);
                    break;
                case REQUESTHOPPACK:
                    app = oin.readChar();
                    content = new RequestNextHop(oin, app);
                    break;
                case NEXTHOPPACK:
                    //app = oin.readChar();
                    String nextHop = oin.readUTF();
                    //System.out.println("Next hop string " + nextHop);
                    content = new NextHopPack(oin, nextHop);
                    break;
                default:
                    content = null;
                    break;
            }
            oin.close();
            bin.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public DatagramPacket toDatagramPacket() {
        DatagramPacket packet = null;

        try {
            ByteArrayOutputStream bout;
            ObjectOutputStream oout;
            byte[] data;

            bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream(bout);

            oout.writeInt(type);         // write type to stream
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

    protected abstract void toObjectOutputStream(ObjectOutputStream oout);


    public byte[] makeHeader(char app) {
        byte[] header = new byte[1];
        header[0] = (byte) app;
        //System.out.println("HEADER :" + header);
        //System.out.println("HEADER[0]" + (char)header[0]);
        //System.out.println("HEADER LENGTH " + header.length);
        return header;
    }

    /**
     * Returns the type of the packet.
     *
     * @return Returns the type of the packet.
     */
    public int getType() {
        //System.out.println("IN THIS GET TYPE ");
        return type;
    }

    public char getApp() {return app;}

//    @Override
//    public abstract PacketContent fromDatagramPacket(DatagramPacket packet);
}