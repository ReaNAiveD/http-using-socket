package server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器端的主类
 *
 * @author liuxingchi
 */
public class HttpServer {

    public static void main(String[] args) {
        try {
            System.out.println("Server <<INFO>> : Server is ready.");
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new ConnectionHandler(socket);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
