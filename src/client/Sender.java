package client;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 抽象出来发请求的简单类
 * 供Connection使用
 *
 * @author liuxingchi
 */
class Sender {

    private DataOutputStream out;

    Sender(Socket socket) {
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求
     *
     * @param request 请求
     */
    void send(String request) {
        try {
            System.out.println("client <<INFO>> : Send a request");
            System.out.println("================================");
            if (request.length() > 1000) {
                System.out.println(request.substring(0, 1000) + "......");
            } else {
                System.out.println(request);
            }
            System.out.println("================================");
            byte[] requestBytes = request.getBytes();
            out.writeInt(requestBytes.length);
            out.write(requestBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
