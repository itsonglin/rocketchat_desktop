package com.rc.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.font.LineMetrics;

/**
 * Created by song on 22/06/2017.
 */
public class HighLightLabel extends JLabel
{
    private String keyWord;
    private Color highLightColor;


    public HighLightLabel()
    {
        this(null, Color.ORANGE);
    }

    public HighLightLabel(String keyWord, Color highLightColor)
    {

        this.keyWord = keyWord;
        this.highLightColor = highLightColor;
    }

    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }

    public void setHighLightColor(Color highLightColor)
    {
        this.highLightColor = highLightColor;
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(getFont());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics fm = getFontMetrics(getFont());
        LineMetrics lm = getFont().getLineMetrics(getText(), g2d.getFontRenderContext());

        // 文本于容器垂直居中
        int y = (int) (fm.getHeight() + lm.getAscent() - lm.getDescent());
        int x = 0;

        // 未提供关键字或关键字为空，则正常绘制
        List<String> strs = splitKeyWord(getText(), keyWord);
        for (String s : strs)
        {
            if (s.equals("\0"))
            {
                g2d.setColor(highLightColor);
                g2d.drawString(keyWord, x , y);
                x += fm.stringWidth(keyWord);
            }
            else
            {
                g2d.setColor(getForeground());
                g2d.drawString(s, x , y);
                x += fm.stringWidth(s);
            }
        }
    }

    private List<String> splitKeyWord(String str, String key)
    {
        List<String> strs = new ArrayList<>();

        if (key.isEmpty())
        {
            strs.add(str);
            return strs;
        }

        int pos = str.indexOf(key);//*第一个出现的索引位置
        int startPos = -key.length();
        while (pos != -1)
        {
            String s = str.substring(startPos + key.length(), pos);
            if (s.length() > 0)
            {
                strs.add(s);
            }

            startPos = pos;
            pos = str.indexOf(key, pos + 1);// 从这个索引往后开始第一个出现的位置

            strs.add("\0");
        }

        if (pos == -1)
        {
            pos = str.length();
            String s = str.substring(startPos + key.length(), pos);

            if (s.length() > 0)
            {
                strs.add(s);
            }
        }

        return strs;
    }


}