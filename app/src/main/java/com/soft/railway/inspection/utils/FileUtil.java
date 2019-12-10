package com.soft.railway.inspection.utils;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.soft.railway.inspection.models.FileModel;
import java.io.File;
import java.util.Timer;

public class FileUtil {
    public static final String FILE_STATUS_NOT_CAN_UPLOADED="0";//文件不需要上传
    public static final String FILE_STATUS_WAIT_UPLOADED="2";//文件等待上传
    public static final String FILE_RANK_HIGH="0";//文件上传级别高
    public static final String FILE_RANK_LOWER="1";//文件上传级别低
    public static final String FILE_TYPE_PHOTO="3";//文件类型为.jpg
    public static final String FILE_TYPE_VIDEO="4";//文件类型为.mp4
    public static final String FILE_TYPE_GPS="1";//文件类型为.gps
    public static final String FILE_TYPE_RECORDER="5";//文件类型为.mp3
    private DownloadManager downloadManager;
    private static FileUtil fileUtil;
    private HttpUtil httpUtil;
    private FileUtil(){}

    public static FileUtil getInstance(){
        if(fileUtil==null){
            synchronized (FileUtil.class){
                if(fileUtil==null){
                    fileUtil=new FileUtil();
                }
            }
        }
        return fileUtil;
    }

    public static boolean deleteFile(String path){
        File file=new File(path);
        if(file.exists()){
            file.delete();
        }
        return true;
    }

    public  boolean insertFile(FileModel fileModel){
       return DBUtil.getInstance().insert(DataUtil.TableNameEnum.SUBMITFILE.toString(), fileModel.getContentValues(fileModel));
    }

    public  boolean updateFile(FileModel fileModel){
        ContentValues contentValues=new ContentValues();
        contentValues.put("filestatus",FileUtil.FILE_STATUS_WAIT_UPLOADED);
        return DBUtil.getInstance().update(DataUtil.TableNameEnum.SUBMITFILE.toString(), contentValues," where fileid=? ",new String[]{fileModel.getFileId()});
    }

    public  boolean updateFileStatus(String workId){
        ContentValues contentValues=new ContentValues();
        contentValues.put("filestatus",FileUtil.FILE_STATUS_WAIT_UPLOADED);
        return DBUtil.getInstance().update(DataUtil.TableNameEnum.SUBMITFILE.toString(), contentValues,"  workid=? ",new String[]{workId});
    }

    public  boolean getFile(FileModel fileModel){
        return DBUtil.getInstance().insert(DataUtil.TableNameEnum.SUBMITFILE.toString(), fileModel.getContentValues(fileModel));
    }

    public  long downFile(Uri uri,String dirPath,String filepath, Context context){
        if(downloadManager==null){
            downloadManager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        if(httpUtil==null){
            httpUtil=HttpUtil.getInstance();
        }

        return httpUtil.downFileHttp(uri,dirPath,filepath,downloadManager);
    }
}
