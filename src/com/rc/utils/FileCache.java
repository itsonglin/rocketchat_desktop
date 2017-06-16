package com.rc.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by song on 2017/6/11.
 */
public class FileCache
{

    private String FILE_CACHE_ROOT_PATH;
    Logger logger = Logger.getLogger(this.getClass());


    public FileCache()
    {
        try
        {
            FILE_CACHE_ROOT_PATH = getClass().getResource("/").getPath() + "/cache/file";
            File file = new File(FILE_CACHE_ROOT_PATH);
            if (!file.exists())
            {
                file.mkdirs();
                System.out.println("创建文件缓存目录：" + file.getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            FILE_CACHE_ROOT_PATH = "./";
        }
    }

    public String tryGetFileCache(String identify, String name)
    {
        File cacheFile = new File(FILE_CACHE_ROOT_PATH + "/" + identify + "_" + name);
        if (cacheFile.exists())
        {
            return cacheFile.getAbsolutePath();
        }

        return null;
    }

    public String cacheFile(String identify, String name, byte[] data)
    {
        File cacheFile = new File(FILE_CACHE_ROOT_PATH + "/" + identify + "_" + name);
        try
        {
            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            outputStream.write(data);
            outputStream.close();
            return cacheFile.getAbsolutePath();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }



}
