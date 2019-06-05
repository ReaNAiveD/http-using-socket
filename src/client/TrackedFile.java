package client;

public class TrackedFile {
    private String filePath;
    private String last_modified;
    private String ETag;

    TrackedFile(String filePath, String last_modified, String ETag){

        this.filePath = filePath;
        this.last_modified = last_modified;
        //生成一个10位的随机数用作文件tag
        this.ETag = ETag;
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
