import java.io.IOException;
import java.net.*;
import java.net.DatagramSocket;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.Scanner;

public class Employee extends Node{

    Employee(int srcPort) {
        try {
            socket = new DatagramSocket(srcPort);
            listener.go();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceipt(DatagramPacket packet) throws Exception {

    }

    public synchronized void start() throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.println("What application would you like to access? A or B? ");
        String appNumber = input.next();
        char app = appNumber.charAt(0);
        sendRequest(app);
    }

    public synchronized void sendRequest(char appNum) throws IOException {
        DatagramPacket packet = new RequestAppContent(appNum).toDatagramPacket();
        //PacketContent content = PacketContent.fromDatagramPacket(packet);

        //TODO: employee always send to nearest router
        packet.setSocketAddress(routerAddress);
        socket.send(packet);
        System.out.println("Request for App " + appNum + " sent");
    }

    public static void main(String[] args) {
        try{
            (new Employee(EMPLOYEE_PORT)).start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
