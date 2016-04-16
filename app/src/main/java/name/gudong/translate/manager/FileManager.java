package name.gudong.translate.manager;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.gudong.translate.BuildConfig;

/**
 * Created by GuDong on 4/5/16 20:20.
 * Contact with gudong.name@gmail.com.
 */
public class FileManager {

    private static final String KEY_CACHE_DIRECTORY = "CACHE_DIRECTORY";

    public File cacheFileOnDisk(Context context,String url,byte[]data){
        File cacheParent = checkCacheParentDirectory(context);
        if(cacheParent!=null){
            return saveFile(cacheParent,url,data);
        }
        return null;
    }

    public File saveFile(File parent,String url,byte[]data){
        String fileName = getFileName(url);
        if(!TextUtils.isEmpty(fileName)){
            File file = new File(parent,fileName);
            saveBytesToFile(data,file);
            return file;
        }
        return null;
    }

    private File checkCacheParentDirectory(Context context){
        File cacheFileDir = new File(context.getCacheDir(),KEY_CACHE_DIRECTORY);
        if(!cacheFileDir.exists()){
            if(cacheFileDir.mkdirs()){
                return cacheFileDir;
            }
        }else{
            return cacheFileDir;
        }
        return null;
    }

    /**
     * gee CacheFile by url
     * @param context Context
     * @param url map url address
     * @return cache file
     */
    public File getCacheFileByUrl(Context context, String url){
        String fileName = getFileName(url);
        File cacheParent = checkCacheParentDirectory(context);
        if(!TextUtils.isEmpty(fileName) && cacheParent!=null && cacheParent.isDirectory()){
            return new File(cacheParent,fileName);
        }
        return null;
    }

    private String getFileName(String url){
        String[]temp = url.split("/");
        String fileName = "";
        if(temp.length != 0){
            fileName = temp[temp.length-1];
        }
        if(!TextUtils.isEmpty(fileName) && fileName.endsWith(".mp3")){
            return fileName;
        }
        return null;
    }

    public boolean resetFileCache(Context context){
        File cacheFileDir = new File(context.getCacheDir(),KEY_CACHE_DIRECTORY);
        if(cacheFileDir.canWrite()){
            File files[] = cacheFileDir.listFiles();
            for (File file:files) {
                boolean isSuc = file.delete();
                if(BuildConfig.DEBUG){
                    if(isSuc){
                        Logger.i("del "+file.getAbsolutePath()+" successfully. save "+file.length()/1024+"kb space");
                    }else{
                        Logger.i("del fail ");
                    }
                }
            }
            Logger.i("del "+cacheFileDir.getAbsolutePath()+" successfully. save "+cacheFileDir.length()/1024+"kb space");
            return true;
        }
        return false;
    }

    private void saveBytesToFile(byte[]data, File destFile){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destFile);
            fos.write(data);
            Logger.i("save "+destFile.getName()+" successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
