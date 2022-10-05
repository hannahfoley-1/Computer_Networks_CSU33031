// Hannah Foley 20332137

import java.io.ObjectOutputStream;

public class Message extends PacketContent{
    String fileName;

    Message(String filename){
        this.fileName = filename;
    }

    @Override
    protected void toObjectOutputStream(ObjectOutputStream out) {
        try{
            out.writeUTF(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return fileName;
    }
}
