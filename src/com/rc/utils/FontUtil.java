package com.rc.utils;

import java.awt.*;

/**
 * Created by song on 17-5-29.
 */
public class FontUtil
{
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
        return new Font("YaHei Consolas Hybrid",  style, size);
    }

}
