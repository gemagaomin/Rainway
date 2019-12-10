package com.soft.railway.inspection.utils;

import android.app.DownloadManager;
import android.net.Uri;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.HeaderModel;
import com.soft.railway.inspection.models.UserModel;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    public final String TYPE_POST="POST";
    public final String TYPE_GET="GET";
    public static String BASE_PATH_OTHER="http:192.168.137.1:/jzgk";
    public static final String BASE_PATH_SERVER="http://218.206.94.241:18989/perfect_jzgk";
    public static final String BASE_PATH_MYSELF="http://192.168.137.1:8090/jzgkDemo_war_exploded";
    public static  String BASE_PATH="http://192.168.137.1:8080/jzgk";
    private OkHttpClient okHttpClient;
    private static HttpUtil httpUtil;
    private DataUtil dataUtil;

    private HttpUtil (){
        if (okHttpClient==null){
            okHttpClient=new OkHttpClient();
            OkHttpClient.Builder builder=new OkHttpClient.Builder();
            builder.readTimeout(30000, TimeUnit.MILLISECONDS);
            builder.writeTimeout(30000,TimeUnit.MILLISECONDS);
            builder.connectTimeout(10000,TimeUnit.MILLISECONDS);
            if(DataUtil.DEBUG){
                //todo 本地地址
                BASE_PATH=BASE_PATH_OTHER;
                if(DataUtil.IS_DEBUG){
                    BASE_PATH=BASE_PATH_SERVER;
                }
            }else{
                BASE_PATH=BASE_PATH_MYSELF;
            }
            dataUtil=DataUtil.getInstance();
            okHttpClient=builder.build();
        }
    }

    public static String getBasePath() {
        return BASE_PATH;
    }

    public static void setBasePath(String basePath) {
        BASE_PATH = basePath;
    }

    public static HttpUtil getInstance(){
        if(httpUtil==null){
            synchronized (HttpUtil.class){
                if(httpUtil==null){
                    httpUtil=new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    public Map<String,String> synch(String url, String type, Map<String,String> params){
        Map map=new HashMap();
        String error="0";
        String result="";
        Response response=null;
        UserModel userModel=dataUtil.getUser();
        HeaderModel headerModel=new HeaderModel(userModel);
        if(TextUtils.isEmpty(type)){
            type=TYPE_GET;
        }
        try{
            String header=AESUtil.AESEncode(JSONObject.toJSONString(headerModel));
            if(TYPE_GET.equals(type)){
                StringBuffer strB=new StringBuffer();
                String urlAndParams="?";
                if(params!=null&&params.size()>0){
                    for(Map.Entry<String, String> entry:params.entrySet()){
                        strB.append(entry.getKey()).append("=").append(AESUtil.AESEncode(entry.getValue())).append("&");
                    }
                }
                strB.append("header").append("=").append(header);
                urlAndParams+=strB.toString();
                Request request;
                if(DataUtil.DEBUG){
                    request=new Request.Builder().url(BASE_PATH+url+urlAndParams).build();
                }else{
                    urlAndParams=AESUtil.AESEncode(urlAndParams);
                    request=new Request.Builder().url(BASE_PATH+url+urlAndParams).build();
                }
                response=okHttpClient.newCall(request).execute();
            }else if(TYPE_POST.equals(type)){
                map.put("header",header);
                map.put("data",AESUtil.AESEncode(params.get("data")));
                RequestBody body= FormBody.create(MediaType.parse("application/json;charset=utf-8"),JSONObject.toJSONString(map));
                Request request=new Request.Builder().url(BASE_PATH+url).post(body).build();
                response=okHttpClient.newCall(request).execute();
            }
            if(response==null){
                error="-1";
            }else{
                if(response.isSuccessful()){
                    result=response.body().string();
                    result=AESUtil.AESDecode(result);
                }
                if(response.code()==500){
                    error="-1";
                }
            }
        }catch (IOException e){
            MyException myException=new MyException();
            myException.buildException(e);
            error="-1";
            result=e.toString();
        }catch (Exception e){
            MyException myException=new MyException();
            myException.buildException(e);
            error="-1";
            result=e.toString();
        }
        map.put("error",error);
        map.put("result",result);
        return map;
    }

    public void asynch(String url,String type,Map<String,String> params,Callback callback){
        if(TextUtils.isEmpty(type)){
            type=TYPE_GET;
        }
        try{
            UserModel userModel=dataUtil.getUser();
            HeaderModel headerModel=new HeaderModel(userModel);
            String header=AESUtil.AESEncode(JSONObject.toJSONString(headerModel));
            Map map=new HashMap();
            if(TYPE_GET.equals(type)){
                StringBuffer strB=new StringBuffer();
                String urlAndParams="?";
                if(params!=null&&params.size()>0){
                    for(Map.Entry<String, String> entry:params.entrySet()){
                        strB.append(entry.getKey()).append("=").append(AESUtil.AESEncode(entry.getValue())).append("&");
                    }
                }
                strB.append("header").append("=").append(header);
                urlAndParams+=strB.toString();
                Request request;
                urlAndParams=AESUtil.AESEncode(urlAndParams);
                request=new Request.Builder().url(BASE_PATH+url+urlAndParams).build();
                okHttpClient.newCall(request).enqueue(callback);
            }else if(TYPE_POST.equals(type)){
                map.put("header",header);
                map.put("data",AESUtil.AESEncode(params.get("data")));
                RequestBody body= FormBody.create(MediaType.parse("application/json;charset=utf-8"),JSONObject.toJSONString(map));
                Request request=new Request.Builder().url(BASE_PATH+url).post(body).build();
                okHttpClient.newCall(request).enqueue(callback);
            }
        }catch (Exception e){
            MyException myException=new MyException();
            myException.buildException(e);
        }

    }

    public String  synchFile( FileModel fileModel){
        String fileType=fileModel.getFileType();
        String filePath=fileModel.getFilePath();
        String fileName=fileModel.getFileName();
        if(FileUtil.FILE_TYPE_VIDEO.equals(fileType)){
            filePath=DataUtil.VIDEO_PATH+filePath.substring(DataUtil.PHOTO_PATH.length(),filePath.length()-4)+".mp4";
        }
        File file=new File(filePath);
        String result="-1";
        if(file.exists()){
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, fileBody)
                    .build();
            String strUrl="getfile";
            if(!DataUtil.DEBUG){
               strUrl="getfilejzgk";
            }
            Request  request=new Request.Builder().url(BASE_PATH+File.separator+"app"+File.separator+strUrl).post(requestBody).build();
            try{
                Response  response=okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    result=response.body().string();
                    if(!TextUtils.isEmpty(result)){
                        JSONObject jsonObject=JSONObject.parseObject(result);
                        result=jsonObject.getString("errorCode");
                    }
                }
            }catch (Exception e){
                MyException myException=new MyException();
                myException.buildException(e);
            }
        }else{
            result="2";//文件不存在
        }
        return result;
    }

    public long downFileHttp(Uri uri, String dirpath, String filePath, DownloadManager downloadManager){
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalPublicDir(dirpath,filePath);
        long id=downloadManager.enqueue(request);
        return  id;
    }

}
