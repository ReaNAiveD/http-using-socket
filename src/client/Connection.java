package client;

import server.exception.ResolveException;

import java.net.Socket;

/**
 * 连接类
 * 这个类代表了一次连接（普通连接或长连接）
 * 有建立连接、结束连接、发送和接受http报文的方法
 *
 * @author liuxingchi, xiepeidong
 */
class Connection {

    private Socket socket;
    private Sender sender;
    private Receiver receiver;
    private RequestMessage requestMessage;
    private ResponseMessage responseMessage;

    Connection() {
        this.requestMessage = new RequestMessage();
        this.responseMessage = new ResponseMessage();
    }

    RequestMessage getRequestMessage() {
        return requestMessage;
    }

    ResponseMessage getResponseMessage() {
        return responseMessage;
    }

    /**
     * 发送请求并接受响应
     * 需要用RequestMessage判断是否为长连接，是长连接将this添加到State.persistentConnections
     */
    void sendAndReceive() {
        if (socket == null) {
            try {
                socket = new Socket(State.host, State.port);
                sender = new Sender(socket);
                receiver = new Receiver(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sender.send(requestMessage.getRequest());
        try {
            responseMessage = ResponseMessage.parse(receiver.receive());
        }catch (ResolveException e){
            e.printStackTrace();
            System.err.println("客户端解析错误！");
        }
        if (isPersistent()) {
            if (!State.persistentConnections.contains(this)) {
                State.persistentConnections.add(this);
            }
        } else {
            State.persistentConnections.remove(this);
            close();
            System.out.println();
            System.out.println("Client <<INFO>> : Close a connection.");
        }
    }

    /**
     * 关闭当前连接
     */
    void close() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前连接是否为长连接
     *
     * @return 当前连接是否为长连接
     */
    private boolean isPersistent() {
        return "keep-alive".equals(requestMessage.getHeaderLines().get("Connection"));
    }

}
