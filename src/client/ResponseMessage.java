package client;


import client.exception.ResolveException;

import java.util.HashMap;

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

    ResponseMessage() {
        headerLines = new HashMap<>();
    }

    /**
     * 将字符串转化到属性
     */
    void setByResponse(String response) throws ResolveException {
        String[] httpLines = response.split("\r\n");
        int headLength = 0;
        try {
            String[] responseBaseInfo = httpLines[0].split(" ", 3);
            version = responseBaseInfo[0].split(State.SPLIT_FLAG)[1];
            headLength += responseBaseInfo[0].length() + 1;
            statusCode = responseBaseInfo[1];
            headLength += statusCode.length() + 1;
            reasonPhrase = responseBaseInfo[2];
            headLength += reasonPhrase.length() + 2;
            int lineCount = 1;
            headLength += 2;
            for (; lineCount < httpLines.length && !"".equals(httpLines[lineCount]); lineCount++) {
                headerLines.put(httpLines[lineCount].split(": ", 2)[0], httpLines[lineCount].split(": ", 2)[1]);
                headLength += httpLines[lineCount].length() + 2;
            }
            entityBody = response.substring(headLength);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new ResolveException();
        }
    }

    public String getVersion() {
        return version;
    }

    String getStatusCode() {
        return statusCode;
    }

    String getReasonPhrase() {
        return reasonPhrase;
    }

    String getHeaderLine(String key) {
        return headerLines.get(key);
    }

    String getEntityBody() {
        return entityBody;
    }

}
