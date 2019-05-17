package client;

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

    /**
     * TODO
     * 将字符串转化到属性
     */
    void setByResponse(String response) {
    }

}
