package client;

import java.io.DataInputStream;
import java.net.Socket;

/**
 * 抽象出来收响应报文的简单类
 * 供Connection使用
 *
 * @author liuxingchi
 */
class Receiver {

    private DataInputStream in;

    Receiver(Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受响应
     *
     * @return 响应
     */
    String receive() {
        String response = null;
        try {
            response = in.readUTF();
            System.out.println();
            System.out.println("client <<INFO>> : Receive a response");
            System.out.println("====================================");
            System.out.println(response);
            System.out.println("====================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
