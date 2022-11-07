import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AppPackContent extends PacketContent  {
    @Override
    protected void toObjectOutputStream(ObjectOutputStream oout) {

    }

    char app;
    int size;
    String appOpeningMessage;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    AppPackContent(char app, int size) {
        type= APPPACK;
        this.app = app;
        this.size= size;
        //TODO: appOpeningMessage =
    }

    AppPackContent(ObjectInputStream oin, char app) throws IOException {
        type= APPPACK;
        //TODO: THIS READING IN BYTES?
        this.app = app;

        if(app == 'A')
        {
            appOpeningMessage = getAppOpeningMessage('A');
        }
        else if (app == 'B'){
            appOpeningMessage = getAppOpeningMessage('B');
        }
        else {
            appOpeningMessage = "Ack packet";
        }
//        if((size= oin.readInt()) < MTU){
//        }
//        else {size = 0;}
//
//        //size = 29;
    }

    public String getAppOpeningMessage(char app){
        if(app == 'A'){
            return "Welcome to Application A - Documents ";
        }
        else if (app == 'B'){
            return "Welcome to Application B - Email ";
        }
        return "";
    }

    /**
     * Returns the type of the packet.
     *
     * @return Returns the type of the packet.
     */
    public int getType() {
        return type;
    }

}
