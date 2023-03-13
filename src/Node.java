//Hannah Foley 20332137

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
    static final int PACKETSIZE = 65536;

    public static final int TYPEPOS = 1;

    public static final int ACKPACKET = 1;
    public static final int REQUESTPACK = 2;
    public static final int APPPACK = 3;
    public static final int HELLOPACK = 4;
    public static final int REQUESTHOPPACK = 5;
    public static final int NEXTHOPPACK = 6;
    public static final int REQUESTROUTINGTABLE = 7;
    public static final int ROUTINGTABLE = 8;


    public static final int ROUTER_PORT = 50080;
    public static final int EMPLOYEE_PORT = 50081;
    public static final int APP_PORT = 50082;
    public static final int CONTROLLER_PORT = 50083;
    public static final int APP2_PORT = 50084;
    public static final int ROUTER2_PORT = 50085;
    public static final int ROUTER3_PORT = 50086;
    public static final int ROUTER4_PORT = 50087;


    public static final InetSocketAddress routerAddress = new InetSocketAddress("router", ROUTER_PORT);
    public static final InetSocketAddress employeeAddress = new InetSocketAddress("employee", EMPLOYEE_PORT);
    public static final InetSocketAddress appAddress = new InetSocketAddress("app", APP_PORT);
    public static final InetSocketAddress app2Address = new InetSocketAddress("app2", APP2_PORT);
    public static final InetSocketAddress controllerAddress = new InetSocketAddress("controller", CONTROLLER_PORT);
    public static final InetSocketAddress router2Address = new InetSocketAddress("router2", ROUTER2_PORT);
    public static final InetSocketAddress router3Address = new InetSocketAddress("router3", ROUTER3_PORT);
    public static final InetSocketAddress router4Address = new InetSocketAddress("router4", ROUTER4_PORT);


    DatagramSocket socket;
    Listener listener;
    CountDownLatch latch;

    Node() {
        latch= new CountDownLatch(1);
        listener= new Listener();
        listener.setDaemon(true);
        listener.start();
    }


    public abstract void onReceipt(DatagramPacket packet) throws Exception;

    /**
     *
     * Listener thread
     *
     * Listens for incoming packets on a datagram socket and informs registered receivers about incoming packets.
     */
    class Listener extends Thread {

        /*
         *  Telling the listener that the socket has been initialized
         */
        public void go() {
            latch.countDown();
        }

        /*
         * Listen for incoming packets and inform receivers
         */
        public void run() {
            try {
                latch.await();
                // Endless loop: attempt to receive packet, notify receivers, etc
                while(true) {
                    DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
                    socket.receive(packet);

                    onReceipt(packet);
                }
            } catch (Exception e) {if (!(e instanceof SocketException)) e.printStackTrace();}
        }
    }
}

