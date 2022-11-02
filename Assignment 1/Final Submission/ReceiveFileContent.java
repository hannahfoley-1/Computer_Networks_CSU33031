//Hannah Foley 20332137

import java.io.*;


/**
 * Class for packet content that represents file information
 *
 */
public class ReceiveFileContent extends PacketContent{
    public final int MTU = 65536;

    String filename;
    int size;
    String fileMessage;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    ReceiveFileContent(String filename, int size, int fileNum) {
        type= RECFILEINFO;
        this.filename = filename;
        this.size= size;
        fileMessage = readInFileMessage(filename);
        filenumber = fileNum;
    }

    ReceiveFileContent(ObjectInputStream oin, int fileNum) throws IOException {
        type= RECFILEINFO;
        filenumber = fileNum;

        if(fileNum == 1)
        {
            filename = "file1.txt";
            fileMessage = readInFileMessage(filename);
        }
        else if (fileNum == 2){
            filename = "file2.txt";
            fileMessage = readInFileMessage(filename);
        }
        else {
            filename = "Ack packet - no file";
            fileMessage = "Ack Packet - no file";
        }
        if((size= oin.readInt()) < MTU){
        }
        else {size = 0;}

        //size = 29;
    }


    private String readInFileMessage (String filename){
        if(filename != null && !filename.equalsIgnoreCase("")) {
            File file = new File(filename);
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultStringBuilder.toString();
        }
        return "";
    }


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
        return fileMessage;
    }

    @Override
    public String returnFileName(){return filename;}

    @Override
    public int[] returnHeader() {
        return new int[0];
    }
}
