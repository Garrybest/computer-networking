package socket.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UDP服务端，不需要监听
 *
 * @Author: Fang Rui
 * @Date: 2018/12/21
 * @Time: 21:28
 */
public class UdpServer {

    private static Logger logger = LogManager.getLogger(UdpServer.class);

    private static final int POOL_SIZE = 20;
    private DatagramSocket socket;

    public UdpServer(DatagramSocket socket) throws SocketException {
        socket.setReceiveBufferSize(1024 * 1024);
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    private class Respond implements Runnable {
        private DatagramPacket packet;

        public Respond(DatagramPacket packet) {
            this.packet = packet;
        }

        @Override
        public void run() {
            try {
                String reply = new String(packet.getData(), 0, packet.getLength()).toUpperCase();
                DatagramPacket replyPacket = new DatagramPacket(reply.getBytes(), packet.getLength(), packet.getAddress(), packet.getPort());
                DatagramSocket socket = new DatagramSocket();
                socket.send(replyPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UdpServer server = new UdpServer(new DatagramSocket(10020));
            logger.info("UDP server is starting.");
            ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
            while (true) {
                byte[] buffer = new byte[1024 * 1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                server.getSocket().receive(packet);
                logger.info("Client data received.");
                pool.submit(server.new Respond(packet));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
