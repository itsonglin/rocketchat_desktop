package com.rc.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


/**
 * Created by song on 15/03/2017.
 */

public class AvatarUtil
{
    private static final Color[] colorArr;

    static
    {
        colorArr = new Color[]{
                new Color(244, 67, 54),
                new Color(233, 30, 99),
                new Color(156, 39, 176),
                new Color(103, 58, 183),
                new Color(63, 81, 181),
                new Color(33, 150, 243),
                new Color(3, 169, 244),
                new Color(0, 188, 212),
                new Color(0, 150, 136),
                new Color(76, 175, 80),
                new Color(139, 195, 74),
                new Color(205, 220, 57),
                new Color(255, 193, 7),
                new Color(255, 152, 0),
                new Color(255, 87, 34),
                new Color(121, 85, 72),
                new Color(158, 158, 158),
                new Color(96, 125, 139)
        };
    }

    private static final String AVATAR_CACHE_ROOT;
    private static final String CUSTOM_AVATAR_CACHE_ROOT;
    private static final int DEFAULT_AVATAR = 0;
    private static final int CUSTOM_AVATAR = 1;

    static
    {
        AVATAR_CACHE_ROOT = new Object().getClass().getResource("/").getPath() + "/cache/avatar";
        File file = new File(AVATAR_CACHE_ROOT);
        if (!file.exists())
        {
            file.mkdirs();
            System.out.println("创建头像缓存目录：" + file.getAbsolutePath());
        }

        CUSTOM_AVATAR_CACHE_ROOT = AVATAR_CACHE_ROOT + "/custom";
        file = new File(CUSTOM_AVATAR_CACHE_ROOT);
        if (!file.exists())
        {
            file.mkdirs();
            System.out.println("创建用户自定义头像缓存目录：" + file.getAbsolutePath());
        }
    }


    public static Image createOrLoadGroupAvatar(String sign, String name)
    {
        Image avatar = getCachedImageAvatar(sign);
        if (avatar == null)
        {
            System.out.println("创建群组头像");
            return createAvatar(sign, name);
        }

        return avatar;
    }


    public static Image createOrLoadUserAvatar(String username)
    {
        Image avatar = getCachedImageAvatar(username);
        if (avatar == null)
        {
            return createAvatar(username, username);
        }

        return avatar;
    }


    private static Image createAvatar(String sign, String name)
    {
        String drawString;
        if (sign.length() > 1)
        {
            drawString = sign.substring(0, 1).toUpperCase() + sign.substring(1, 2).toLowerCase();
        } else
        {
            drawString = sign;
        }

        try
        {
            int width = 50;
            int height = 50;

            // 创建BufferedImage对象
            Font font = FontUtil.getDefaultFont(24, Font.PLAIN);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 获取Graphics2D
            Graphics2D g2d = image.createGraphics();

            // 抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 画图
            g2d.setBackground(getColor(name));
            g2d.clearRect(0, 0, width, height);

            // 文字
            g2d.setFont(font);
            g2d.setPaint(new Color(255, 255, 255));
            FontMetrics fm = g2d.getFontMetrics(font);
            int strWidth = fm.stringWidth(drawString);
            int strHeight = fm.getHeight();
            int x = (width - strWidth) / 2;
            g2d.drawString(drawString, x, strHeight);
            g2d.dispose();

            File file = new File(AVATAR_CACHE_ROOT + "/" + sign + ".jpg");
            ImageIO.write(image, "jpg", file);

            return image;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }


    private static Color getColor(String username)
    {
        int position = username.length() % colorArr.length;
        return colorArr[position];
    }

    public static void saveAvatar(byte[] data, String username)
    {
        saveAvatar(data, username, CUSTOM_AVATAR);
    }

    private static void saveAvatar(byte[] data, String username, int type)
    {
        String path = "";
        if (type == DEFAULT_AVATAR)
        {
            path = AVATAR_CACHE_ROOT + "/" + username + ".jpg";
        } else if (type == CUSTOM_AVATAR)
        {
            path = CUSTOM_AVATAR_CACHE_ROOT + "/" + username + ".jpg";
        }
        else
        {
            throw new RuntimeException("类型不存在");
        }

        File avatarPath = new File(path);

        try
        {
            FileOutputStream outputStream = new FileOutputStream(avatarPath);
            outputStream.write(data);
            outputStream.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Image getCachedImageAvatar(String username)
    {
        if (customAvatarExist(username))
        {
            String path = CUSTOM_AVATAR_CACHE_ROOT + "/" + username + ".jpg";
            ImageIcon imageIcon = new ImageIcon(path);
            return imageIcon.getImage();
        } else if (defaultAvatarExist(username))
        {
            String path = AVATAR_CACHE_ROOT + "/" + username + ".jpg";
            ImageIcon imageIcon = new ImageIcon(path);
            return imageIcon.getImage();
        } else
        {
            return null;
        }
    }


    public static boolean customAvatarExist(String username)
    {
        String path = CUSTOM_AVATAR_CACHE_ROOT + "/" + username + ".jpg";
        File file = new File(path);
        return file.exists();
    }

    public static boolean defaultAvatarExist(String username)
    {
        String path = AVATAR_CACHE_ROOT + "/" + username + ".jpg";
        File file = new File(path);
        return file.exists();
    }

    public static void deleteCustomAvatar(String username)
    {
        String path = CUSTOM_AVATAR_CACHE_ROOT + "/" + username + ".jpg";

        File file = new File(path);
        if (file.exists())
        {
            file.delete();
        }
    }

    public static void main(String[] a)
    {
        System.out.println(AvatarUtil.customAvatarExist("song"));
    }
}
