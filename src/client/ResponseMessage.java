package client;

import server.exception.ResolveException;

import java.util.HashMap;
import java.util.List;

/**
 * 响应报文类
 *
 * @author linxingchi
 */
class ResponseMessage {

    private String version;
    private String statusCode;
    private String reasonPhrase;
    private HashMap<String, String> headerLines;
    private String entityBody;

    /**
     * TODO
     * 将字符串转化到属性
     */
    void setByResponse(String response) throws ResolveException {
        String[] httpLines = response.split("\r\n");
        try {
            String[] responseBaseInfo = httpLines[0].split(" ");
            version = responseBaseInfo[0].split("/")[1];
            statusCode = responseBaseInfo[1];
            reasonPhrase = responseBaseInfo[2];
            int lineCount = 1;
            for(; !httpLines[lineCount].equals(""); lineCount++){
                headerLines.put(httpLines[lineCount].split(": ")[0], httpLines[lineCount].split(": ")[1]);
            }
            lineCount++;
            StringBuilder stringBuilder = new StringBuilder();
            for (; lineCount < httpLines.length; lineCount++){
                stringBuilder.append(httpLines[lineCount]);
                if (lineCount != httpLines.length - 1) stringBuilder.append("\r\n");
            }
            entityBody = stringBuilder.toString();
        } catch (IndexOutOfBoundsException e){
            throw new ResolveException();
        }
    }

    public static ResponseMessage parse(String response) throws ResolveException{
        ResponseMessage result = new ResponseMessage();
        result.setByResponse(response);
        return result;
    }

}
