package client;

import java.util.ArrayList;
import java.util.List;

public class Resources {

    /**
     * 列表中的文件有时间修改属性
     */
    private static List<TrackedFile> trackedFiles = new ArrayList<>();


    /**
     * 该方法用来初始化安排的资源
     */

    public static boolean isTracked(String filePath){
        boolean flag = false;
        for(TrackedFile file: trackedFiles){
            if(file.getFilePath().equals(filePath)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static TrackedFile getTrackedFileByPath(String filePath){
        for(TrackedFile file: trackedFiles){
            if(file.getFilePath().equals(filePath)){
                return file;
            }
        }
        return null;
    }

    public static void addTrackedFile(TrackedFile file){
        trackedFiles.add(file);
    }
}
