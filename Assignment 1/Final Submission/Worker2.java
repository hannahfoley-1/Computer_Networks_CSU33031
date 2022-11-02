import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;

public class Worker2 extends Worker{
    Worker2(int port) {
        super(Node.WORKER2_PORT);
    }

    public static void main(String[] args) {
        try {
            (new Worker(WORKER2_PORT)).start();
            //(new Worker(WORKER2_PORT)).start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onReceipt(DatagramPacket packet){
        try {
            System.out.println("Packet was received");

            PacketContent content= PacketContent.fromDatagramPacket(packet);

            //acknowledge receipt
//            DatagramPacket response;
//            response = new AckPacketContent("OK - Worker received this").toDatagramPacket();
//            response.setSocketAddress(packet.getSocketAddress());
//            socket.send(response);

            int packetType = content.getType();
            //int packetTopic = content.getTopic();
            switch(packetType) {
                case PacketContent.ACKPACKET:
                    System.out.print("Received Ack packet");
                    break;
                case PacketContent.GETFILEINFO:
                    System.out.println("Sending File");
                    //todo: send file back to ingress
                    sendFile(packet);
                    break;
                case PacketContent.RECFILEINFO:
                    System.out.println("Error: Wrong packet reached worker");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void sendFile(DatagramPacket packet) throws Exception {
        //DatagramPacket packet;

        PacketContent content = PacketContent.fromDatagramPacket(packet);
        //int [] header = content.returnHeader();
        int fileNumber = content.getFileNumber();
        System.out.println("filenumber:" + fileNumber);
        //TODO: GET FILE SIZE and find a way of sending file asked fo4
        String filename = "";
        if (fileNumber == 1) {
            filename = "file1.txt";
        } else {
            filename = "file2.txt";
        }
        File file = new File(filename);
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int size = fin.read(buffer);
        if (size == -1) {
            fin.close();
            throw new Exception("Problem with File Access:" + filename);
        }
        System.out.println("File size: " + buffer.length);

        //TODO: SEND PACKET TO INGRESS
        //FileInfoContent fileContent = new FileInfoContent(filename, size);
        ReceiveFileContent fileContent = new ReceiveFileContent(filename, size, fileNumber);

        System.out.println("Sending packet w/ name: " + filename + " & length: " + size); // Send packet with file name and length
        //System.out.println("Client port " + CLIENT_PORT);
        //System.out.println("Ingress port " + INGRESS_PORT);
        //System.out.println("Worker port " + WORKER_PORT);
        packet = fileContent.toDatagramPacket();
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Packet sent");
        //this.wait();
        fin.close();
    }
}
