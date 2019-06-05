package server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 抽象出来发请求的简单类
 * 供Connection使用
 *
 * @author xiepeidong
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
     * 发送请求，0成功，1失败
     *
     * @param response 请求
     */
    int send(String response) {
        try {
            System.out.println();
            System.out.println("client <<INFO>> : Send a response");
            System.out.println("=================================");
            System.out.println(response);
            System.out.println("=================================");
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            out.writeInt(responseBytes.length);
            out.write(responseBytes);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 处理回应
     *
     * @param response 回应报文
     */
    void processResponse(String response) {

    }

}

