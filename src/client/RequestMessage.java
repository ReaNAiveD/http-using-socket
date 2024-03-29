package client;


import java.util.HashMap;
import java.util.Map;

/**
 * 请求报文类
 *
 * @author linxingchi
 */
class RequestMessage {

    private String method;
    private String url;
    private String version;
    private HashMap<String, String> headerLines;
    private String entityBody;

    void setMethod(String method) {
        this.method = method;
        System.out.println("client <<INFO>> : Set the request method to " + method + ".");
    }

    void setUrl(String url) {
        this.url = url;
        System.out.println("client <<INFO>> : Set the request URL to " + url + ".");
    }

    void setVersion(String version) {
        this.version = version;
        System.out.println("client <<INFO>> : Set the request version to HTTP/" + version + ".");
    }

    HashMap<String, String> getHeaderLines() {
        return headerLines;
    }

    /**
     * 初始化header并增加header键值对
     *
     * @param key   键
     * @param value 值
     */
    void initHeaderLine(String key, String value) {
        headerLines = new HashMap<>(7);
        addHeaderLine(key, value);
    }

    /**
     * 增加header键值对
     *
     * @param key   键
     * @param value 值
     */
    void addHeaderLine(String key, String value) {
        headerLines.put(key, value);
        System.out.println("client <<INFO>> : Set the \"" + key + "\" to \"" + value + "\" in header.");
        State.input = Input.HEADER;
    }

    void setEntityBody(String entityBody) {
        this.entityBody = entityBody;
    }

    /**
     * todo
     * 将属性转化到字符串
     *
     * @return 请求的字符串
     */
    String getRequest() {
        StringBuilder requestBuilder = new StringBuilder();
        //请求行
        requestBuilder.append(method);
        requestBuilder.append(' ');
        requestBuilder.append(url);
        requestBuilder.append(' ');
        requestBuilder.append("HTTP/");
        requestBuilder.append(version);
        requestBuilder.append("\r\n");
        //首部
        for (Map.Entry<String, String> entry : headerLines.entrySet()) {
            requestBuilder.append(entry.getKey());
            requestBuilder.append(": ");
            requestBuilder.append(entry.getValue());
            requestBuilder.append("\r\n");
        }
        requestBuilder.append("\r\n");
        //实体
        requestBuilder.append(entityBody);
        return String.valueOf(requestBuilder);
    }

}
