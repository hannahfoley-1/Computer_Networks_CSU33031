//Hannah Foley 20332137

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReceiveFileContent extends PacketContent{
    String filename;
    int size;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    ReceiveFileContent(String filename, int size) {
        type= RECFILEINFO;
        this.filename = filename;
        this.size= size;
    }

    ReceiveFileContent(ObjectInputStream oin) throws IOException {
        type= RECFILEINFO;
        filename= oin.readUTF();
        //size= oin.readInt();
        size = 29;
    }


//    /**
//     * Constructs an object out of a datagram packet.
//     * @param packet Packet that contains information about a file.
//     */
//    protected ReceiveFileContent(ObjectInputStream oin) {
//        try {
//            type= RECFILEINFO;
//            filename= oin.readUTF();
//            size= oin.readInt();
//        }
//        catch(Exception e) {e.printStackTrace();}
//    }


    @Override
    protected void toObjectOutputStream(ObjectOutputStream out) {
        try{
            out.writeUTF(filename);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return null;
    }
}
