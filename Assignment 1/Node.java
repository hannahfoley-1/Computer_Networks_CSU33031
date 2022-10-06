//Hannah Foley 20332137

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
    static final int PACKETSIZE = 65536;

    public static final int INGRESS_PORT = 50090;
    public static final int CLIENT_PORT = 50091;
    public static final int WORKER_PORT = 50092;

    public static final InetSocketAddress ingressAddress = new InetSocketAddress("part2ingress", INGRESS_PORT);
    public static final InetSocketAddress clientAddress = new InetSocketAddress("part2client", CLIENT_PORT);
    public static final InetSocketAddress workerAddress = new InetSocketAddress("part2worker", WORKER_PORT);
    public static final InetSocketAddress worker2Address = new InetSocketAddress("part2worker2", WORKER_PORT);


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

