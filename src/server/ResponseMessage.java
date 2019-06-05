package server;

import server.utils.DateTool;

import java.text.DateFormat;
import java.util.Date;
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

    ResponseMessage(String version, String statusCode, String reasonPhrase){
        headerLines = new HashMap<>();
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.entityBody = "";
        setServerHeadLines();
    }

    /**
     * TODO
     * 将属性转化到字符串
     *
     * @return 响应的字符串
     */
    String getResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/").append(version).append(" ").append(statusCode).append(" ").append(reasonPhrase).append("\r\n");
        for (String key : headerLines.keySet()){
            stringBuilder.append(key).append(": ").append(headerLines.get(key)).append("\r\n");
        }
        stringBuilder.append("\r\n");
        stringBuilder.append(entityBody);
        return stringBuilder.toString();
    }

    private void setServerHeadLines(){
        Date currentTime = new Date();
        DateFormat dateFormat = DateTool.getHttpRespondDateHeaderFormat();
        headerLines.put("Date", dateFormat.format(currentTime));
    }

    public void setContentType(String contentType){
        headerLines.put("Content-Type", contentType);
    }

    public void AddHeaderLines(String key, String value){
        headerLines.put(key, value);
    }

    public void setEntityBody(String entityBody){
        this.entityBody = entityBody;
    }

}
