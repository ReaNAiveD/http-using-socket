package client;

import client.exception.ResolveException;
import client.exception.ResourceStoreException;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 连接类
 * 这个类代表了一次连接（普通连接或长连接）
 * 有建立连接、结束连接、发送和接受http报文的方法
 *
 * @author liuxingchi, xiepeidong
 */
class Connection {

    private static int number = 0;
    private int id;

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
            responseMessage.setByResponse(receiver.receive());
            //根据状态码处理
            if ("200".equals(responseMessage.getStatusCode())) {
                if (responseMessage.getHeaderLine(State.TYPE_HEADER) != null) {
                    if (State.TEXT_TYPE.equals(responseMessage.getHeaderLine(State.TYPE_HEADER).split(State.SPLIT_FLAG)[0])) {
                        System.out.println(responseMessage.getEntityBody());
                    } else {
                        //计算文件名
                        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
                        MimeType type = allTypes.forName(responseMessage.getHeaderLine("Content-Type").split(";")[0]);
                        String extension = type.getExtension();
                        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                        Date current = new Date();
                        String timeString = dateFormat.format(current);
                        for (int i = 0; ; i++) {
                            String storeUrl = processUrl("/" + timeString + i + extension);
                            File file = new File(storeUrl);
                            if (!file.exists()) {
                                //不存在同名文件时解码并存储
                                OutputStream outputStream = new FileOutputStream(file);
                                Base64.Decoder decoder = Base64.getDecoder();
                                byte[] b = decoder.decode(responseMessage.getEntityBody());
                                outputStream.write(b);
                                outputStream.flush();
                                outputStream.close();
                                System.out.println("Client <<INFO>> : Receive a resource. Explore it in " + storeUrl);
                                break;
                            }
                        }
                    }
                }
            } else {
                System.err.println(responseMessage.getStatusCode() + " " + responseMessage.getReasonPhrase());
            }
        } catch (ResolveException e) {
            e.printStackTrace();
            System.err.println("客户端解析错误！");
        } catch (MimeTypeException e) {
            e.printStackTrace();
            System.err.println("MIME类型不受支持！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("文件夹不存在！");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("文件写入发生错误！");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("内容解析失败！");
        } catch (ResourceStoreException e) {
            e.printStackTrace();
            System.err.println("存储目录出现问题！");
        }
        if (isPersistent()) {
            if (!State.persistentConnections.values().contains(this)) {
                Connection.number++;
                id = Connection.number;
                State.persistentConnections.put(number, this);
                System.out.println("Client <<INFO>> : The Connection is not closed.");
            }
        } else {
            State.tempConnection = null;
            State.persistentConnections.remove(id);
            close();
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
        if (requestMessage.getHeaderLines().containsKey(State.CONNECTION_HEADER)) {
            return State.PERSISTENT_HEADER.equals(requestMessage.getHeaderLines().get(State.CONNECTION_HEADER));
        } else {
            return requestMessage.getVersion().equals(State.VALID_VERSION.get(1));
        }
    }

    private String processUrl(String originUrl) throws ResourceStoreException {
        File dire = new File("clientResources");
        if (!dire.exists()) {
            if (!dire.mkdir()) {
                throw new ResourceStoreException();
            }
        } else if (dire.exists() && dire.isDirectory()) {
            return "clientResources" + originUrl;
        } else {
            throw new ResourceStoreException();
        }
        return "clientResources" + originUrl;
    }

}
