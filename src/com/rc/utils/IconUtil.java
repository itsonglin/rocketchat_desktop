package com.rc.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by song on 2017/6/7.
 */
public class IconUtil
{
    public static ImageIcon getIcon(Object context, String path)
    {
        return getIcon(context, path, -1, -1);
    }

    public static ImageIcon getIcon(Object context, String path, int width)
    {
        return getIcon(context, path, width, width);
    }

    public static ImageIcon getIcon(Object context, String path, int width, int height)
    {
        URL url = context.getClass().getResource(path);
        if (url == null)
        {
            return null;
        }

        ImageIcon imageIcon = new ImageIcon(url);

        if (width > 0 && height > 0)
        {
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }

        return imageIcon;
    }
}
