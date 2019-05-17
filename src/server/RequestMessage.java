package server;

import server.exception.ResolveException;

import java.io.File;
import java.util.HashMap;

/**
 * 请求报文类
 *
 * @author linxingchi
 */
class RequestMessage {

    //GET,POST or other(Capital)
    private String method;
    private String url;
    private String version;
    private HashMap<String, String> headerLines;
    private String entityBody;

    public String getMethod() {return method;}

    public String getUrl() {return url;}

    public String getVersion() {return version;}

    public String getHeaderLine(String key) {return headerLines.get(key);}

    public String getEntityBody() {return entityBody;}

    /**
     * TODO
     * 将字符串转化到属性
     */
    void setByRequest(String request) throws ResolveException {
        headerLines = new HashMap<>(7);
        String[] array = request.split("\r\n");
        //请求行数
        int lineCount = array.length;
        try {
            String[] firstLine = array[0].split(" ");
            if (firstLine.length < 3) throw new ResolveException();
            method = firstLine[0];
            url = firstLine[1];
            version = firstLine[2].split("/")[1];
            //寻找头区域
            int line = 1;
            for(; line < lineCount && !array[line].equals(""); line++){
                headerLines.put(array[line].split(": ")[0], array[line].split(": ")[1]);
            }
            line++;
            StringBuilder stringBuilder = new StringBuilder();
            if (line < lineCount) {
                for (; line < lineCount; line++) {
                    stringBuilder.append(array[line]);
                    if (line != lineCount - 1) stringBuilder.append("\r\n");
                }
            }
            entityBody = stringBuilder.toString();
            print();
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace(System.err);
            throw new ResolveException();
        }
    }

    public static RequestMessage parse(String request) throws ResolveException {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setByRequest(request);
        return requestMessage;
    }

    public void print(){
        System.out.println(method);
        System.out.println(url);
        System.out.println(version);
        for (String key : headerLines.keySet()){
            System.out.println(key + ": " + headerLines.get(key));
        }
        System.out.println(entityBody);
    }

}
