package socket.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Fang Rui
 * @Date: 2018/12/22
 * @Time: 16:53
 */
public class TcpClient {

    private static Logger logger = LogManager.getLogger(TcpClient.class);

    @Test
    public void testSendingMessage() {
        try {
            String message = "abcdefghijklmn";
            Socket socket = new Socket(InetAddress.getLocalHost(), 12000);
            socket.setSoTimeout(5 * 1000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            out.flush();

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String reply = in.readUTF();
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
                    Socket socket = new Socket(InetAddress.getLocalHost(), 12000);
                    socket.setSoTimeout(5 * 1000);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF(message);
                    out.flush();

                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String reply = in.readUTF();
                    logger.info("Output message: " + reply);

                    socket.close();
                    assert reply.equals(message.toUpperCase());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
