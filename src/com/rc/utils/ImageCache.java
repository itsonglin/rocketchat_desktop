package com.rc.utils;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by song on 2017/6/11.
 */
public class ImageCache
{
    private String CACHE_ROOT_PATH;
    Logger logger = Logger.getLogger(this.getClass());


    public ImageCache()
    {
        try
        {
            CACHE_ROOT_PATH = getClass().getResource("/").getPath() + "/cache/image";
            File file = new File(CACHE_ROOT_PATH);
            if (!file.exists())
            {
                file.mkdirs();
                System.out.println("创建图片缓存目录：" + file.getAbsolutePath());
            }
        } catch (Exception e)
        {
            CACHE_ROOT_PATH = "./";
        }
    }

    public static void main(String[] a)
    {
        ImageCache cache= new ImageCache();
        String url = "https://rc.shls-leasing.com/file-upload/7yQLcXcPy6dkB2Rfb/ad_image.jpg.jpg?rc_uid=Ni7bJcX3W8yExKSa3&rc_token=Kffvg9XoLpH2o38wMZ3QmKUV1_nDFNAmpPiaH-uDHwM";
        cache.request("7yQLcXcPy6dkB2Rfb", url, new CacheRequestListener()
        {
            @Override
            public void onSuccess(ImageIcon icon)
            {
                System.out.println("success");
            }

            @Override
            public void onFailed(String why)
            {
                System.out.println("failed");
            }
        });
    }

    public void request(String identify, String url, CacheRequestListener listener)
    {
        File cacheFile = new File(CACHE_ROOT_PATH + "/" + identify);
        if (cacheFile.exists())
        {
            ImageIcon icon = new ImageIcon(cacheFile.getAbsolutePath());
            listener.onSuccess(icon);
        }
        else
        {
            byte[] data = HttpUtil.download(url);
            try
            {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                outputStream.write(data);
                outputStream.close();

                ImageIcon icon = new ImageIcon(cacheFile.getAbsolutePath());
                listener.onSuccess(icon);

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }


    public interface CacheRequestListener
    {
        void onSuccess(ImageIcon icon);

        void onFailed(String why);
    }


}
