//Hannah Foley 20332137

import javax.swing.*;
import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * The class is the basis for packet contents of various types.
 *
 *
 */
public abstract class PacketContent {

    public static final int ACKPACKET= 1;
    public static final int GETFILEINFO= 2;
    public static final int RECFILEINFO = 3;

    public static final int FILE1 = 1;
    public static final int FILE2 = 2;

    public final int MTU = 65536;

    //TODO: other header stuff??
    public static final int OFFSET = 1;


    int type;
    //int [] header;
    int filenumber;


    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet to analyse.
     */
    public static PacketContent fromDatagramPacket(DatagramPacket packet) {
        PacketContent content= null;

        try {
            int type;
            int filenumber;
            String filename = "";

            byte[] data;
            ByteArrayInputStream bin;
            ObjectInputStream oin;

            data= packet.getData();  // use packet content as seed for stream
            bin= new ByteArrayInputStream(data);
            oin= new ObjectInputStream(bin);

            type= oin.readInt();  // read type from beginning of packet
            filenumber = oin.readInt();
            System.out.println("File number " + filenumber);
//            System.out.println(data.length);
//            int payload = oin.read(data, 1, data.length-10);
//            System.out.println("PAYLOAD " + payload);


            switch(type) {   // depending on type create content object
                case ACKPACKET:
                    content= new AckPacketContent(oin);
                    break;
                case GETFILEINFO:
                    content= new FileInfoContent(oin, filenumber);
                    break;
                case RECFILEINFO:
                    content = new ReceiveFileContent(oin, filenumber);
                    break;
                default:
                    content= null;
                    break;
            }
            oin.close();
            bin.close();

        }
        catch(Exception e) {e.printStackTrace();
            }

        return content;
    }


    /**
     * This method is used to transform content into an output stream.
     *
     * @param out Stream to write the content for the packet to.
     */
    protected abstract void toObjectOutputStream(ObjectOutputStream out);

    /**
     * Returns the content of the object as DatagramPacket.
     *
     * @return Returns the content of the object as DatagramPacket.
     */
    public DatagramPacket toDatagramPacket() {
        DatagramPacket packet= null;

        try {
            ByteArrayOutputStream bout;
            ObjectOutputStream oout;
            byte[] data;

            bout= new ByteArrayOutputStream();
            oout= new ObjectOutputStream(bout);

            System.out.println("Making packet now with type " + type);
            oout.writeInt(type);         // write type to stream
            //this.packfilenumber = filenumber;

            System.out.println("Making packet now with filenumber:" + filenumber);
            oout.writeInt(filenumber); //write out file number request
            toObjectOutputStream(oout);  // write content to stream depending on type


            oout.flush();

            byte[] header = makeHeader();
            data= bout.toByteArray(); // convert content to byte array

            //concat these byte arrays header + data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(header);
            outputStream.write(data);

            byte[] packetByteArray = outputStream.toByteArray( );

            packet = new DatagramPacket(packetByteArray, OFFSET, data.length);
            oout.close();
            bout.close();
        }
        catch(Exception e) {e.printStackTrace();}

        return packet;
    }


    public abstract int[] returnHeader();


    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public abstract String toString();

    /**
     * Returns the type of the packet.
     *
     * @return Returns the type of the packet.
     */
    public int getType() {
        return type;
    }

    public int getFileNumber() {
        //return filenumber;
        byte[] header = makeHeader();
        return Byte.toUnsignedInt(header[0]);
    }

    public int getOffset() { return OFFSET; }

    public byte[] makeHeader(){
        byte[] header = new byte[1];
        Integer fileno = filenumber;
        header[0] = fileno.byteValue();
        return header;
    }

    public byte[] getHeader() { return makeHeader(); }

    public int getFilenumber() {return filenumber;}

    public String returnFileName(){return "";}


}
