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
public class ImageCache
{
    public static final int THUMB = 0;
    public static final int ORIGINAL = 1;

    private String IMAGE_CACHE_ROOT_PATH;
    Logger logger = Logger.getLogger(this.getClass());


    public ImageCache()
    {
        try
        {
            IMAGE_CACHE_ROOT_PATH = getClass().getResource("/").getPath() + "/cache/image";
            File file = new File(IMAGE_CACHE_ROOT_PATH);
            if (!file.exists())
            {
                file.mkdirs();
                System.out.println("创建图片缓存目录：" + file.getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            IMAGE_CACHE_ROOT_PATH = "./";
        }
    }

    public ImageIcon tryGetThumbCache(String identify)
    {
        File cacheFile = new File(IMAGE_CACHE_ROOT_PATH + "/" + identify + "_thumb");
        if (cacheFile.exists())
        {
            ImageIcon icon = new ImageIcon(cacheFile.getAbsolutePath());
            return icon;
        }

        return null;
    }


    /**
     * 异步获取图像缩略图
     *
     * @param identify
     * @param url
     * @param listener
     */
    public void requestThumbAsynchronously(String identify, String url, CacheRequestListener listener)
    {
        requestImage(THUMB, identify, url, listener);

    }

    /**
     * 异步获取图像原图
     *
     * @param identify
     * @param url
     * @param listener
     */
    public void requestOriginalAsynchronously(String identify, String url, CacheRequestListener listener)
    {
        requestImage(ORIGINAL, identify, url, listener);
    }


    private void requestImage(int requestType, String identify, String url, CacheRequestListener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                File cacheFile;
                if (requestType == THUMB)
                {
                    cacheFile = new File(IMAGE_CACHE_ROOT_PATH + "/" + identify + "_thumb");
                }
                else
                {
                    cacheFile = new File(IMAGE_CACHE_ROOT_PATH + "/" + identify);
                }

                if (cacheFile.exists())
                {
                    System.out.println("获取   " + cacheFile.getAbsolutePath());
                    ImageIcon icon = new ImageIcon(cacheFile.getAbsolutePath());
                    listener.onSuccess(icon);
                }
                else
                {
                    try
                    {
                        byte[] data;

                        // 本地上传的文件，则从原上传路径复制一份分缓存目录
                        if (url.startsWith("file://"))
                        {
                            String originUrl = url.substring(7);
                            FileInputStream fileInputStream = new FileInputStream(originUrl);
                            data = new byte[fileInputStream.available()];
                            fileInputStream.read(data);
                        }
                        // 接收的图像，从服务器获取并缓存
                        else
                        {
                            System.out.println("服务器获取");
                            data = HttpUtil.download(url);
                        }


                        if (data == null)
                        {
                            logger.debug("图像获取失败");
                        }

                        Image image = ImageIO.read(new ByteArrayInputStream(data));

                        // 生成缩略图并缓存
                        createThumb(image, identify);

                        if (requestType == THUMB)
                        {
                            ImageIcon icon = new ImageIcon(cacheFile.getAbsolutePath());
                            listener.onSuccess(icon);
                        }

                        // 缓存原图
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(IMAGE_CACHE_ROOT_PATH + "/" + identify));
                        fileOutputStream.write(data);

                        if (requestType == ORIGINAL)
                        {
                            ImageIcon icon = new ImageIcon(cacheFile.getAbsolutePath());
                            listener.onSuccess(icon);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 生成图片缩略图
     *
     * @param image
     * @param identify
     */
    public void createThumb(Image image, String identify)
    {
        try
        {
            int[] imageSize = getImageSize(image);
            int destWidth = imageSize[0];
            int destHeight = imageSize[1];

            float scale = imageSize[0] * 1.0F / imageSize[1];

            if (imageSize[0] > imageSize[1] && imageSize[0] > 200)
            {
                destWidth = 200;
                destHeight = (int) (destWidth / scale);
            }
            else if (imageSize[0] < imageSize[1] && imageSize[1] > 200)
            {
                destHeight = 200;
                destWidth = (int) (destHeight * scale);
            }

            // 开始读取文件并进行压缩
            BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);

            tag.getGraphics().drawImage(image.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH), 0, 0, null);

            File cacheFile = new File(IMAGE_CACHE_ROOT_PATH + "/" + identify + "_thumb");
            FileOutputStream out = new FileOutputStream(cacheFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static int[] getImageSize(Image image)
    {

        if (image == null)
        {
            return new int[]{10, 10};
        }
        int result[] = {0, 0};
        try
        {
            result[0] = image.getWidth(null); // 得到源图宽
            result[1] = image.getHeight(null); // 得到源图高
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }


    public interface CacheRequestListener
    {
        void onSuccess(ImageIcon icon);

        void onFailed(String why);
    }


}
