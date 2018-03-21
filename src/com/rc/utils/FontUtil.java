package com.rc.utils;

import com.rc.db.service.MessageService;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by song on 17-5-29.
 */
public class FontUtil
{
    private static Font font;

    static
    {
        if (OSUtil.getOsType() == OSUtil.Windows)
        {
            font = new Font("微软雅黑", Font.PLAIN, 14);
        }
        else if (OSUtil.getOsType() == OSUtil.Mac_OS)
        {
            font = new Font("PingFang SC", Font.PLAIN, 14);
        }
        else if (OSUtil.getOsType() == OSUtil.Linux)
        {
            String fontName = "宋体";
            GraphicsEnvironment environment;
            environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fonts = environment.getAvailableFontFamilyNames();//获得系统字体
            for (int i = 0; i < fonts.length; i++)
            {
                if (fonts[i].equals("YaHei Consolas Hybrid"))
                {
                    fontName = "YaHei Consolas Hybrid";
                    break;
                }
            }

            font = new Font(fontName, Font.PLAIN, 14);
        }
    }

    public static Font getDefaultFont()
    {
        return getDefaultFont(14, Font.PLAIN);
    }

    public static Font getDefaultFont(int size)
    {
        return getDefaultFont(size, Font.PLAIN);
    }

    public static Font getDefaultFont(int size, int style)
    {
        return font.deriveFont(style, size);
        //return new Font("YaHei Consolas Hybrid",  style, size);
        //return new Font("微软雅黑", style, size);
    }
}
