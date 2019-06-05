package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 这个类用来管理本地文件，确定资源的位置变动，修改时间，
 * 来完成对状态码301、302、304的应对
 */
public class Resources {

    /**
     * 旧地址，新地址
     * 永久移动资源，用来对应状态码301
     */
    private static HashMap<String, String> perMovedFileMap = new HashMap<>();
    /**
     * 暂时移动的资源，用来对应状态码302
     */
    private static HashMap<String, String> tempMovedFileMap = new HashMap<>();
    /**
     * 列表中的文件有时间修改属性，用来应对状态码304
     */
    private static List<TrackedFile> trackedFiles = new ArrayList<>();


    /**
     * 该方法用来初始化安排的资源
     */
    public static void initialPreparedResources(){

        perMovedFileMap.put("/testFor301.txt","/Backup/testFor301.txt");
        tempMovedFileMap.put("/testFor302.txt","/Backup/testFor302.txt");
        TrackedFile file = new TrackedFile("/testFor304.txt");
        trackedFiles.add(file);
    }

    public static boolean isPerMoved(String filePath){
        return perMovedFileMap.containsKey(filePath);
    }
    public static String getMovedPath_Per(String filePath){
        return perMovedFileMap.get(filePath);
    }

    public static boolean isTempMoved(String filePath){
        return tempMovedFileMap.containsKey(filePath);
    }
    public static String getMovedPath_Temp(String filePath){
        return tempMovedFileMap.get(filePath);
    }

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

    public static boolean isPathMightCauseError(String filePath){
        if (filePath.equals("/error")){
            return true;
        }
        return false;
    }



}
