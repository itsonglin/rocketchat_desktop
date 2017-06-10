package com.rc.utils;

import com.rc.components.Colors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


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


    public static Image createGroupAvatar(String sign, String name)
    {
        return createAvatar(sign, name);
    }


    public static Image createOrLoadUserAvatar(String username)
    {
        Image avatar = getImageAvatar(username);
        if (avatar == null)
        {
            return createAvatar(username, username);
        }
        return avatar;
    }

    public static Image createAvatar(String sign, String name)
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
            int width = 40;
            int height = 40;

            // 创建BufferedImage对象
            Font font = FontUtil.getDefaultFont(19, Font.PLAIN);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 获取Graphics2D
            Graphics2D g2d = image.createGraphics();

            // 抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 画图
            g2d.setBackground(getColor(name));
            g2d.setPaint(new Color(255, 255, 255));
            g2d.clearRect(0, 0, width, height);
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics(font);
            int strWidth = fm.stringWidth(drawString);
            int strHeight = fm.getHeight();
            int x = (width - strWidth) / 2;
            g2d.drawString(drawString, x, strHeight);
            g2d.dispose();

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
        String path = "";
        File avatarPath = new File(path + "/" + "avatar");
        if (!avatarPath.exists())
        {
            avatarPath.mkdirs();
        }

        try
        {
            FileOutputStream outputStream = new FileOutputStream(avatarPath + "/" + username + ".jpg");
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

    public static Image getImageAvatar(String username)
    {
        return null;
    }

    public static boolean avatarExist(String username)
    {
        return false;
    }

    public static void deleteAvatar(String username)
    {

    }
}
