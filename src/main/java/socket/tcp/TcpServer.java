package socket.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import socket.udp.UdpServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Fang Rui
 * @Date: 2018/12/22
 * @Time: 17:27
 */
public class TcpServer {

    private static Logger logger = LogManager.getLogger(UdpServer.class);

    private static final int POOL_SIZE = 20;
    private ServerSocket listener;

    public TcpServer(int port) throws IOException {
        listener = new ServerSocket(port);
    }

    public ServerSocket getListener() {
        return listener;
    }

    private class Respond implements Runnable {
        private Socket socket;

        public Respond(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String reply = in.readUTF().toUpperCase();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(reply);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            TcpServer server = new TcpServer(12000);
            logger.info("TCP server is starting.");
            ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
            while (true) {
                Socket socket = server.getListener().accept();
                pool.submit(server.new Respond(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
