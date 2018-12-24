package socket.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Fang Rui
 * @Date: 2018/12/22
 * @Time: 16:53
 */
public class UdpClient {

    private static Logger logger = LogManager.getLogger(UdpClient.class);

    @Test
    public void testSendingMessage() {
        try {
            String message = "abcdefghijklmn";
            InetAddress address = InetAddress.getLocalHost();
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5 * 1000);

            DatagramPacket sendMessage = new DatagramPacket(message.getBytes(), message.getBytes().length, address, 10020);
            socket.send(sendMessage);

            byte[] buffer = new byte[1024 * 1024];
            DatagramPacket receiveMessage = new DatagramPacket(buffer, buffer.length);
            socket.receive(receiveMessage);

            String reply = new String(receiveMessage.getData(), 0, receiveMessage.getLength());

            logger.info("Output message: " + reply);

            assert reply.equals(message.toUpperCase());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConcurrentSendingMessage() throws UnknownHostException {
        String message = "abcdefghijklmn";
        InetAddress address = InetAddress.getLocalHost();
        ExecutorService pool = Executors.newFixedThreadPool(5);
        while (true) {
            pool.submit(() -> {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    socket.setSoTimeout(5 * 1000);

                    DatagramPacket sendMessage = new DatagramPacket(message.getBytes(), message.getBytes().length, address, 10020);
                    socket.send(sendMessage);

                    byte[] buffer = new byte[1024 * 1024];
                    DatagramPacket receiveMessage = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receiveMessage);

                    String reply = new String(receiveMessage.getData(), 0, receiveMessage.getLength());

                    logger.info("Output message: " + reply);

                    assert reply.equals(message.toUpperCase());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }
}
