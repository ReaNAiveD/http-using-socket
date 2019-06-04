package server;

import server.exception.ResolveException;
import server.utils.InputStreamTool;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
            //System.out.println("responseMessage == null : " + (responseMessage == null));
            //System.out.println("responseMessage.getResponse() == null : " + (responseMessage.getResponse() == null));
            sender.send(responseMessage.getResponse());
        } catch (ResolveException e) {
            e.printStackTrace();
            responseMessage = new ResponseMessage("1.1", "400", "Bad Request");
            sender.send(responseMessage.getResponse());
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage = new ResponseMessage("1.1", "500", "Internal Server Error");
            sender.send(responseMessage.getResponse());
        }
    }

    /**
     * TODO
     * 处理请求
     */
    private void dealWithRequest() {
        if (requestMessage.getMethod().equals("GET")) {
            File requestedFile = new File(processUrl(requestMessage.getUrl()));
            if (requestedFile.exists()) {
                try {
                    responseMessage = new ResponseMessage("1.1", "200", "OK");
                    String contentType = Files.probeContentType(requestedFile.toPath());
                    responseMessage.setContentType(contentType);
                    InputStream inputStream = new FileInputStream(requestedFile);
                    byte[] data = InputStreamTool.readAllBytes(inputStream);
                    inputStream.close();
                    //如果是text类型的
                    if (contentType.split("/")[0].equals("text")) {
                        responseMessage.setEntityBody(new String(data, StandardCharsets.UTF_8));
                    } else {
                        Base64.Encoder encoder = Base64.getEncoder();
                        responseMessage.setEntityBody(encoder.encodeToString(data));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    responseMessage = new ResponseMessage("1.1", "500", "Internal Server Error");
                }
            } else {
                responseMessage = new ResponseMessage("1.1", "404", "Not Found");
            }
        }

        if (requestMessage.getMethod().equals("POST")) {
            if (requestMessage.getHeaderLine("Content-Type") == null) {
                System.out.println("Server <<INFO>> : Receive a POST request to PATH " + requestMessage.getUrl() + " without Content-Type. Content: ");
                System.out.println(requestMessage.getEntityBody());
                responseMessage = new ResponseMessage("1.1", "200", "OK");
            } else {
                if (requestMessage.getHeaderLine("Content-Type").equals("text")) {
                    System.out.println("Server <<INFO>> : Receive a POST request which is " + requestMessage.getHeaderLine("Content-Type") + " to PATH " + requestMessage.getUrl() + ". Content: ");
                    System.out.println(requestMessage.getEntityBody());
                } else {
                    try {
                        MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
                        MimeType type = mimeTypes.forName(requestMessage.getHeaderLine("Content-Type").split(";")[0]);
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
                                byte[] b = decoder.decode(requestMessage.getEntityBody());
                                outputStream.write(b);
                                outputStream.flush();
                                outputStream.close();
                                System.out.println("Server <<INFO>> : Receive a POST request to PATH " + requestMessage.getUrl() + " with " + requestMessage.getHeaderLine("Content-Type") + ". ");
                                System.out.println("Server <<INFO>> : Received Resources have been saved to " + storeUrl);
                                break;
                            }
                        }
                        responseMessage = new ResponseMessage("1.1", "200", "OK");
                    } catch (MimeTypeException e) {
                        System.err.println("Server <<EXCEPTION>> : Unsupported MimeType");
                        responseMessage = new ResponseMessage("1.1", "415", "Unsupported Media Type");
                    } catch (FileNotFoundException e) {
                        System.err.println("Server <<EXCEPTION>> : Server Receive Fold Not Exist");
                        responseMessage = new ResponseMessage("1.1", "500", "Internal Server Error");
                    } catch (IOException e) {
                        System.err.println("Server <<EXCEPTION>> : IO EXCEPTION");
                        responseMessage = new ResponseMessage("1.1", "500", "Internal Server Error");
                    } catch (Exception e) {
                        e.printStackTrace();
                        responseMessage = new ResponseMessage("1.1", "500", "Internal Server Error");
                    }
                }
            }
        }
    }

    private String processUrl(String originUrl) {
        if (originUrl.equals("")) {
            return "resources" + getDefaultUrl();
        }
        return "resources" + originUrl;
    }

    private String getDefaultUrl() {
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
