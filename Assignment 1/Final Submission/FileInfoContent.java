//Hannah Foley 20332137

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents file requests
 *
 */
public class FileInfoContent extends PacketContent {

    String filename;
    int size;
    int [] header = new int [2];

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    FileInfoContent(String filename, int size, int fileNum) {
        type= GETFILEINFO;
        this.filename = filename;
        this.size= size;
        filenumber = fileNum;
        header[0] = filenumber;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected FileInfoContent(ObjectInputStream oin, int filenum) {
        try {
            type= GETFILEINFO;

            filenumber = filenum;
            size= oin.readInt();
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(String.valueOf(filenumber));
            oout.writeUTF(filename);
            oout.writeInt(size);
        }
        catch(Exception e) {e.printStackTrace();}
    }


    /**
     * Returns the content of the packet as String.
     *
     * @return Returns the content of the packet as String.
     */
    public String toString() {
        return "Filename: " + filename + " - Size: " + size;
    }

    /**
     * Returns the file name contained in the packet.
     *
     * @return Returns the file name contained in the packet.
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Returns the file size contained in the packet.
     *
     * @return Returns the file size contained in the packet.
     */
    public int getFileSize() {
        return size;
    }

    @Override
    public int[] returnHeader() {
        return header;
    }

    public int getFilenumber(){
        return filenumber;
    }
}