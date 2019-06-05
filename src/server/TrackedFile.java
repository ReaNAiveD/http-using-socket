package server;

import server.utils.DateTool;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class TrackedFile {

    private String filePath;
    private String last_modified;
    private String ETag;

    TrackedFile(String filePath){

        this.filePath = filePath;
        Date currentTime = new Date();
        DateFormat dateFormat = DateTool.getHttpRespondDateHeaderFormat();
        this.last_modified = dateFormat.format(currentTime);
        //生成一个10位的随机数用作文件tag
        this.ETag = getRandom(10);
    }



    private static String getRandom(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }
}
