package com.rc.utils;

import com.rc.forms.MainFrame;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Created by song on 17-5-29.
 */
public class FontUtil
{
    private static Font font;

    static{
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(FontUtil.class.getResourceAsStream("/fonts/yahei.ttf")));
        }catch (Exception e)
        {
            System.out.println("找不到指定文件");
            font = new Font("微软雅黑", Font.PLAIN, 14);
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

        return font;
        //return new Font("YaHei Consolas Hybrid",  style, size);
        //return new Font("微软雅黑", style, size);
    }

}
