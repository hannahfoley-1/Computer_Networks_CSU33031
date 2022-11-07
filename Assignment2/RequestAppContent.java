import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RequestAppContent extends PacketContent{

    //static char app;
    int size;
    RequestAppContent(char appAsked) {
        type= REQUESTPACK;
        app = appAsked;
        //System.out.println("Packet made with app " + app);
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected RequestAppContent(ObjectInputStream oin, char app) {
        try {
            type= REQUESTPACK;
            this.app = app;
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
            //oout.writeUTF(String.valueOf(app));
            oout.writeInt(size);
        }
        catch(Exception e) {e.printStackTrace();}
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
