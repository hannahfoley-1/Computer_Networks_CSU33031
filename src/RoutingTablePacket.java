import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Scanner;

public class RoutingTablePacket extends PacketContent{

    RoutingTablePacket(String [][] table)
    {
        this.type = ROUTINGTABLE;
        this.table = table;
    }

    @Override
    protected void toObjectOutputStream(ObjectOutputStream oout) throws IOException {
        //byte [][] bytesTable = new byte[table.length][];
        for(int i = 0; i < table.length; i++)
        {
            //byte [] currentStringArrayToByteArray;
            String currentString = "";
            for (int j = 0; j < table[i].length; j++)
            {
                currentString += table[i][j] + ":";
            }
            currentString += "!"; //end of line
            //currentStringArrayToByteArray = currentString.getBytes();
            oout.writeUTF(currentString);
            //bytesTable[i] = currentStringArrayToByteArray;
        }
//        for(int i = 0; i < bytesTable.length; i++)
//        {
//            oout.write(bytesTable[i]);
//        }
    }

    public RoutingTablePacket(ObjectInputStream oin) {
        ArrayList<String[]> arrays = new ArrayList<>();
        Scanner scanner = new Scanner(oin);
        try {
            type= ROUTINGTABLE;
            String string;
            //int count = 0;
            //System.out.println(oin.toString());
            while(oin.available() > 28)
            //while (scanner.hasNext())
            {
                //System.out.println("Available bytes " + oin.available());
                string = oin.readUTF();
                String from = "";
                String through = "";
                String destination = "";
                String [] split = string.split(":");
                from = split[0];
                through = split[1];
                destination = split[2];

                //System.out.println(string + " from " + from + " through " + through + " destination " + destination);

                String [] line = {from, through, destination};
                arrays.add(line);
                //System.out.println("Available bytes " + oin.available());
            }
            ////System.out.println("Starts filling table");
            this.table = new String[arrays.size()][3];
            for(int i = 0; i < arrays.size(); i++)
            {
                this.table[i]=arrays.get(i);
            }
        }
        catch(Exception e) {e.printStackTrace();}
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
            byte [][] bytesTable = new byte[table.length][];
            for(int i = 0; i < table.length; i++)
            {
                byte [] currentStringArrayToByteArray;
                String currentString = "";
                for (int j = 0; j < table[i].length; j++)
                {
                    currentString += table[i][j] + ":";
                }
                currentString += "!"; //end of line
                currentStringArrayToByteArray = currentString.getBytes();
                bytesTable[i] = currentStringArrayToByteArray;
            }

            //oout.writeUTF(node);
            //oout.writeUTF(destination);
            toObjectOutputStream(oout);  // write content to stream depending on type
            oout.flush();

            //make byte arrays
            //System.out.println("Making header with app " + app);
            //byte[] header = makeHeader(app);
            data = bout.toByteArray(); // convert content to byte array

            //concat these byte arrays header + data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //outputStream.write(header);
            int byteOffset = 0;
            for(int i = 0; i < bytesTable.length; i++)
            {
                outputStream.write(bytesTable[i]);
                byteOffset += bytesTable[i].length;
            }
            outputStream.write(data);

            byte[] packetByteArray = outputStream.toByteArray();

            //System.out.println("PACKET BYTE ARRAY SIZE" + byteOffset);
            packet = new DatagramPacket(packetByteArray, byteOffset, data.length);
            oout.close();
            bout.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    @Override
    public String[][] getTable() {
        //System.out.println("Entering correct get table");
        if(table == null){
            System.out.println("NULL TABLE???");
        }
        return table;
    }

    String[][] extractTable(ObjectInputStream oin)
    {
        return table;
    }

    @Override
    public String getNode() {
        return null;
    }
}
