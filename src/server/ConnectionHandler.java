package server;

import server.RequestMessage;
import server.ResponseMessage;
import server.exception.ResolveException;
import server.utils.InputStreamTool;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

/**
 * 连接线程类
 *
 * @author liuxingchi, xiepeidong
 */
class ConnectionHandler extends Thread {

    private Socket socket;
    private Sender sender;
    private Receiver receiver;
    private RequestMessage requestMessage;
    private ResponseMessage responseMessage;

    ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println();
        System.out.println("Server <<INFO>> : Get a connection.");
        System.out.println("Server <<INFO>> : Client address is " + socket.getRemoteSocketAddress() + ".");
        try {
            sender = new Sender(socket);
            receiver = new Receiver(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        do {
            receiveAndSend();
        } while (isPersistent());
    }

    /**
     * 接受请求并发送响应
     */
    private void receiveAndSend() {
        try {
            requestMessage = RequestMessage.parse(receiver.receive());
            dealWithRequest();
            sender.send(responseMessage.getResponse());
        }
        catch (ResolveException e){
            e.printStackTrace();
            responseMessage = new ResponseMessage("1.1", "400", "Bad Request");
            sender.send(responseMessage.getResponse());
        }
    }

    /**
     * TODO
     * 处理请求
     */
    private void dealWithRequest() {
        if (requestMessage.getMethod().equals("GET")){
            File requestedFile = new File(processUrl(requestMessage.getUrl()));
            if (requestedFile.exists()){
                try {
                    responseMessage = new ResponseMessage("1.1", "200", "OK");
                    String contentType = Files.probeContentType(requestedFile.toPath());
                    responseMessage.setContentType(contentType);
                    InputStream inputStream = new FileInputStream(requestedFile);
                    byte[] data = InputStreamTool.readAllBytes(inputStream);
                    inputStream.close();
                    //如果是text类型的
                    if (contentType.split("/")[0].equals("text")){
                        responseMessage.setEntityBody(new String(data, StandardCharsets.UTF_8));
                    }
                    else {
                        Base64.Encoder encoder = Base64.getEncoder();
                        responseMessage.setEntityBody(encoder.encodeToString(data));
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                    responseMessage = new ResponseMessage("1.1", "500", "Internal Server Error");
                }
            }
            else {
                responseMessage = new ResponseMessage("1.1", "404", "Not Found");
            }
        }
    }

    private String processUrl(String originUrl){
        if (originUrl.equals("")){
            return "resources" + getDefaultUrl();
        }
        return "resources" + originUrl;
    }

    private String getDefaultUrl(){
        return "/hello.txt";
    }

    /**
     * 判断当前连接是否为长连接
     *
     * @return 当前连接是否为长连接
     */
    private boolean isPersistent() {
        return "keep-alive".equals(requestMessage.getHeaderLine("Connection"));
    }

}
